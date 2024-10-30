package com.theoghn.mtgtreasury.ui.decks.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoghn.mtgtreasury.models.Deck
import com.theoghn.mtgtreasury.models.card.MtgCard
import com.theoghn.mtgtreasury.services.CardsService
import com.theoghn.mtgtreasury.services.DecksService
import com.theoghn.mtgtreasury.services.InventoryService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.HashMap
import javax.inject.Inject

@HiltViewModel
class DeckViewModel @Inject constructor(
    private val decksService: DecksService,
    private val cardsService: CardsService,
    private val inventoryService: InventoryService
) : ViewModel() {
    val uiState = MutableStateFlow<DeckScreenUiState>(DeckScreenUiState.Loading)

    fun getCards(deckId: String) {
        viewModelScope.launch {
            val inventory = inventoryService.getInventory()

            val deck = decksService.getDeck(deckId)
            val missingCardsIds = deck.cards.keys.toList().filter { !inventory.contains(it) }

            cardsService.getCardsByIds(deck.cards.keys.toList())
                .let { incomingCards ->
                    val newCards = incomingCards.map {
                        it.copy(
                            qty = if (deck.cards.contains(it.id)) {
                                deck.cards[it.id]!!
                            } else {
                                0
                            }
                        )
                    }
                    if (newCards.isEmpty()) {
                        uiState.update { DeckScreenUiState.Empty }
                    } else {
                        uiState.update { DeckScreenUiState.DeckUi(newCards, missingCardsIds, deck) }
                    }
                }
        }
    }

    fun removeCardFromDeck(cardId: String, deleted: Boolean = false) {
        if (uiState.value is DeckScreenUiState.DeckUi) {
            val currentState = uiState.value as DeckScreenUiState.DeckUi
            val deck = currentState.deck
            var currentCards = currentState.cards
            val currentDeck = deck.cards.toMutableMap()
            currentDeck[cardId] = (currentDeck[cardId] ?: 0) - 1

            if (currentDeck[cardId]!! <= 0 || deleted) {
                currentDeck.remove(cardId)
                currentCards = currentCards.filter { card -> card.id != cardId }
            }

            uiState.update {
                DeckScreenUiState.DeckUi(
                    currentCards,
                    currentState.missingCardsIds,
                    deck.copy(cards = HashMap(currentDeck))
                )
            }
        }
    }

    fun addCardToDeck(cardId: String) {
        if (uiState.value is DeckScreenUiState.DeckUi) {
            val deck = (uiState.value as DeckScreenUiState.DeckUi).deck
            val currentDeck = deck.cards.toMutableMap()
            currentDeck[cardId] = (currentDeck[cardId] ?: 0) + 1
            uiState.update {
                DeckScreenUiState.DeckUi(
                    (it as DeckScreenUiState.DeckUi).cards,
                    (it).missingCardsIds,
                    deck.copy(cards = HashMap(currentDeck))
                )
            }
        }
    }

    fun updateDeck() {
        Log.d("update", "update deck")
        viewModelScope.launch {
            if (uiState.value is DeckScreenUiState.DeckUi) {
                val deck = (uiState.value as DeckScreenUiState.DeckUi).deck
                decksService.updateDeck(deck.id, deck.name, deck.deckImage, deck.cards)
            }
        }
    }

    fun deleteDeck() {
        viewModelScope.launch {
            if (uiState.value is DeckScreenUiState.DeckUi) {
                val deck = (uiState.value as DeckScreenUiState.DeckUi).deck
                decksService.deleteDeck(deck.id)
            }
        }
    }
}

sealed class DeckScreenUiState {
    data class DeckUi(
        val cards: List<MtgCard>,
        val missingCardsIds: List<String>,
        val deck: Deck
    ) : DeckScreenUiState()

    object Loading : DeckScreenUiState()
    object Empty : DeckScreenUiState()
}