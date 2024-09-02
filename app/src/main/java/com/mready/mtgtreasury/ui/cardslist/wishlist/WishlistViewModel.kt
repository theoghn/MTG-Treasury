package com.mready.mtgtreasury.ui.cardslist.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.services.CardsService
import com.mready.mtgtreasury.services.WishlistService
import com.mready.mtgtreasury.ui.cardslist.InventoryScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject



@OptIn(FlowPreview::class)
@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val cardsService: CardsService,
    private val wishlistService: WishlistService
) : ViewModel() {
    private val initialCards = MutableStateFlow(emptyList<MtgCard>())
    val uiState = MutableStateFlow<WishlistScreenUiState>(WishlistScreenUiState.Loading)
    var searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            wishlistService.getWishlistFlow().collect { wishlist ->
                val wishlistCards = cardsService.getCardsByIds(wishlist)

                delay(100)
                initialCards.update { wishlistCards }

                if (wishlistCards.isEmpty()) {
                    uiState.update { WishlistScreenUiState.Empty }
                } else {
                    uiState.update { WishlistScreenUiState.WishlistUi(wishlistCards) }
                }
                filterCardsByQuery()
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery.update { newQuery }
        filterCardsByQuery()
    }

    fun filterCardsByQuery() {
        val filteredCards =
            initialCards.value.filter { it.name.contains(searchQuery.value, ignoreCase = true) }
        if (filteredCards.isEmpty()) {
            uiState.update { WishlistScreenUiState.Empty }
        } else {
            uiState.update { WishlistScreenUiState.WishlistUi(filteredCards) }
        }
    }
}

sealed class WishlistScreenUiState {
    data class WishlistUi(
        val cards: List<MtgCard>,
    ) : WishlistScreenUiState()

    object Loading : WishlistScreenUiState()
    object Empty : WishlistScreenUiState()
}