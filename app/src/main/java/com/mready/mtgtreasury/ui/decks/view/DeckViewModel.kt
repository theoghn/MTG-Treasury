package com.mready.mtgtreasury.ui.decks.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.models.Deck
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.services.CardsService
import com.mready.mtgtreasury.services.DecksService
import com.mready.mtgtreasury.services.InventoryService
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
    //    val cards = MutableStateFlow(emptyList<MtgCard>())
//    val missingCardsIds = MutableStateFlow(emptyList<String>())
//    val deck = MutableStateFlow<Deck?>(null)
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
                    if(newCards.isEmpty()){
                        uiState.update { DeckScreenUiState.Empty }
                    }
                    else{
                        uiState.update { DeckScreenUiState.DeckUi(newCards, missingCardsIds, deck) }
                    }
                }
        }
    }

    fun removeCardFromDeck(cardId: String, deleted: Boolean = false) {
//        val currentDeck = deck.value?.cards?.toMutableMap() ?: mutableMapOf()
//        currentDeck[cardId] = (currentDeck[cardId] ?: 0) - 1
//        if (currentDeck[cardId]!! <= 0 || deleted) {
//            currentDeck.remove(cardId)
//            cards.update { cards.value.filter { it.id != cardId } }
//        }
//        deck.update { deck.value?.copy(cards = HashMap(currentDeck)) }
    }

    fun addCardToDeck(cardId: String) {
//        val currentDeck = deck.value?.cards?.toMutableMap() ?: mutableMapOf()
//        currentDeck[cardId] = (currentDeck[cardId] ?: 0) + 1
//        deck.update { deck.value?.copy(cards = HashMap(currentDeck)) }
    }

    fun updateDeck() {
        Log.d("update","update deck")
//        viewModelScope.launch {
//            deck.value?.let { decksService.updateDeck(it.id, it.name, it.deckImage, it.cards) }
//        }
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