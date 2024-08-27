package com.mready.mtgtreasury.ui.decks.view

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
import javax.inject.Inject

@HiltViewModel
class DeckViewModel @Inject constructor(
    private val decksService: DecksService,
    private val cardsService: CardsService,
    private val inventoryService: InventoryService
) : ViewModel() {
    val cards = MutableStateFlow(emptyList<MtgCard>())
    val missingCardsIds = MutableStateFlow(emptyList<String>())
    val deck = MutableStateFlow<Deck?>(null)

    fun getCards(deckId: String) {
        viewModelScope.launch {
            deck.update { decksService.getDeck(deckId) }
            val inventory = inventoryService.getInventory()
            if (deck.value != null) {
                missingCardsIds.update { deck.value!!.cards.keys.toList().filter { !inventory.contains(it) } }
            }

            cardsService.getCardsByIds(deck.value?.cards?.keys?.toList() ?: emptyList())
                .let { incomingCards ->
                    val newCards = incomingCards.map {
                        it.copy(
                            qty = if (deck.value?.cards?.contains(it.id) == true) {
                                deck.value!!.cards[it.id]!!
                            } else {
                                0
                            }
                        )
                    }
                    cards.update { newCards }
                }
        }
    }
}