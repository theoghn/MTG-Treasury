package com.theoghn.mtgtreasury.ui.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoghn.mtgtreasury.models.card.MtgCard
import com.theoghn.mtgtreasury.services.CardsService
import com.theoghn.mtgtreasury.services.InventoryService
import com.theoghn.mtgtreasury.services.WishlistService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    var updateCardQtyJob: Job? = null
    var updateWishlistJob : Job? = null

    fun getCard(id: String) {
        viewModelScope.launch {
            val card = cardsService.getCard(id)
            val combinedFlow = wishlistService.getWishlistFlow()
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
                    when (it) {
                        is CardScreenUiState.Loading -> CardScreenUiState.CardUi(
                            mtgCard = card,
                            isWishlisted = favoriteState,
                            qtyInInventory = inventoryState
                        )
                        is CardScreenUiState.CardUi ->
                            it.copy(
                                isWishlisted = favoriteState,
                            )
                    }
                }
            }
        }
    }


    fun addCardToInventory(cardId: String) {
        uiState.update {
            when (it) {
                is CardScreenUiState.CardUi -> {
                    it.copy(qtyInInventory = it.qtyInInventory + 1)
                }
                else -> it
            }
        }

        if(uiState.value is CardScreenUiState.CardUi){
            updateCardQuantity(cardId, (uiState.value as CardScreenUiState.CardUi).qtyInInventory)
        }
    }

    fun removeCardFromInventory(cardId: String) {
        uiState.update {
            when (it) {
                is CardScreenUiState.CardUi -> {
                    it.copy(qtyInInventory = it.qtyInInventory - 1)
                }

                else -> it
            }
        }

        if(uiState.value is CardScreenUiState.CardUi){
            updateCardQuantity(cardId, (uiState.value as CardScreenUiState.CardUi).qtyInInventory)
        }
    }

    fun updateCardQuantity(cardId: String, quantity: Int) {
        updateCardQtyJob?.cancel()
        updateCardQtyJob =  viewModelScope.launch {
            delay(500)
            inventoryService.updateCardQuantity(cardId, quantity)
        }
    }

    fun updateWishlist(cardId: String, isWishlisted: Boolean) {
        updateWishlistJob?.cancel()
        updateWishlistJob = viewModelScope.launch {
            delay(500)
            if (isWishlisted) {
                wishlistService.addCardToWishlist(cardId)
            } else {
                wishlistService.removeCardFromWishlist(cardId)
            }
        }
    }
}

sealed class CardScreenUiState {
    data class CardUi(
        val mtgCard: MtgCard,
        val isWishlisted: Boolean = false,
        val qtyInInventory: Int = 0
    ) : CardScreenUiState()

    data object Loading : CardScreenUiState()
}