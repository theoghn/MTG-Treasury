package com.mready.mtgtreasury.ui.decks.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.services.CardsService
import com.mready.mtgtreasury.services.DecksService
import com.mready.mtgtreasury.services.InventoryService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckCreationViewModel @Inject constructor(
    private val decksService: DecksService,
    private val inventoryService: InventoryService,
    private val cardsService: CardsService
) : ViewModel() {
    val inventoryCards = MutableStateFlow<List<MtgCard>>(emptyList())
    val deckCards = MutableStateFlow<Map<String, Int>>(emptyMap())
    val deckName = MutableStateFlow("")
    val initialized = MutableStateFlow(false)

    fun initialize(deckId : String) {
        viewModelScope.launch {
            val deck = decksService.getDeck(deckId)
            deckCards.update { deck.cards }
            deckName.update { deck.name }

        }
        getInventoryCards()
    }

    fun updateDeckName(name: String) {
        deckName.update { name }
    }

    fun addCardToDeck(cardId: String) {
        val currentDeck = deckCards.value.toMutableMap()
        currentDeck[cardId] = (currentDeck[cardId] ?: 0) + 1
        deckCards.update { currentDeck }
    }

    fun removeCardFromDeck(cardId: String) {
        val currentDeck = deckCards.value.toMutableMap()
        currentDeck[cardId] = (currentDeck[cardId] ?: 0) - 1
        if (currentDeck[cardId] == 0) {
            currentDeck.remove(cardId)
        }
        deckCards.update { currentDeck }
    }

    fun updateDeck(deckId: String, deckName: String, displayCardId: String, deckList: Map<String, Int>) {
        val deckHashMap = if (deckList.isEmpty()) {
            hashMapOf()
        } else {
            HashMap(deckList)
        }

        val img = inventoryCards.value.find { it.id == displayCardId }?.imageUris?.borderCrop ?: ""

        viewModelScope.launch {
            decksService.updateDeck(deckId, deckName, img, deckHashMap)
        }
    }

    fun createDeck(deckName: String, displayCardId: String, deckList: Map<String, Int>) {
        val deckHashMap = if (deckList.isEmpty()) {
            hashMapOf()
        } else {
            HashMap(deckList)
        }

        val img = inventoryCards.value.find { it.id == displayCardId }?.imageUris?.borderCrop ?: ""

        viewModelScope.launch {
            decksService.createDeck(deckName, img, deckHashMap)
        }
    }

    fun getInventoryCards() {
        viewModelScope.launch {
            val inventory = inventoryService.getInventory()
            var cards = cardsService.getCardsByIds(inventory.keys.toList())

            cards = cards.map {
                it.copy(
                    qty = if (inventory.contains(it.id)) {
                        inventory[it.id]!!
                    } else {
                        0
                    }
                )
            }
            inventoryCards.update { cards }

            delay(400)
            initialized.update { true }
        }
    }
}