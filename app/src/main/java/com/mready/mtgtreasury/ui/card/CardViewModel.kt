package com.mready.mtgtreasury.ui.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.services.CardsService
import com.mready.mtgtreasury.services.InventoryService
import com.mready.mtgtreasury.services.WishlistService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CardViewModel @Inject constructor(
    private val cardsService: CardsService,
    private val wishlistService: WishlistService,
    private val inventoryService: InventoryService
) : ViewModel() {
    val uiState = MutableStateFlow<CardScreenUiState>(CardScreenUiState.Loading)

    fun getCard(id: String) {

        viewModelScope.launch {
            val card = cardsService.getCard(id)
            val combinedFlow = wishlistService.getWishlist()
                .combine(inventoryService.getInventoryFlow()) { wishlist, inventory ->
                    Pair(wishlist, inventory)
                }
            combinedFlow.collect {
                val favoriteState = it.first.contains(card.id)
                val inventoryState = if (it.second.contains(card.id)) {
                    it.second[card.id]!!
                } else {
                    0
                }

                uiState.update {
                    CardScreenUiState.CardUi(
                        mtgCard = card,
                        isFavorite = favoriteState,
                        qtyInInventory = inventoryState
                    )
                }
            }
        }
    }

    fun addCardToInventory(cardId: String) {
        viewModelScope.launch {
            inventoryService.addCardToInventory(cardId)
        }
    }

    fun removeCardFromInventory(cardId: String, currentQuantity: Int) {
        viewModelScope.launch {
            inventoryService.removeCardFromInventory(cardId = cardId, currentQuantity = currentQuantity)
        }
    }

    fun addCardToWishlist(cardId: String) {
        viewModelScope.launch {
            wishlistService.addCardToWishlist(cardId)
        }
    }

    fun removeCardFromWishlist(cardId: String) {
        viewModelScope.launch {
            wishlistService.removeCardFromWishlist(cardId)
        }
    }
}

sealed class CardScreenUiState {
    data class CardUi(
        val mtgCard: MtgCard,
        val isFavorite: Boolean = false,
        val qtyInInventory: Int = 0
    ) : CardScreenUiState()

    data object Loading : CardScreenUiState()
}