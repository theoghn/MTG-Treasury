package com.mready.mtgtreasury.ui.cardslist.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.services.CardsService
import com.mready.mtgtreasury.services.ExternalUserService
import com.mready.mtgtreasury.services.UserService
import com.mready.mtgtreasury.services.WishlistService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val cardsService: CardsService,
    private val wishlistService: WishlistService,
    private val userService: UserService,
    private val externalUserService: ExternalUserService
) : ViewModel() {
    private val initialCards = MutableStateFlow(emptyList<MtgCard>())
    val uiState = MutableStateFlow<WishlistScreenUiState>(WishlistScreenUiState.Loading)
    var searchQuery = MutableStateFlow("")

    fun initialize(userId: String) {
        if (uiState.value == WishlistScreenUiState.Loading) {
            when (userId) {
                userService.getUID() -> getLocalUserWishlist()
                else -> getForeignUserWishlist(userId)
            }
        }
    }

    private fun getForeignUserWishlist(userId: String){
        viewModelScope.launch {
            val wishlistIds = externalUserService.getUserWishlist(userId)
            val wishlistCards = cardsService.getCardsByIds(wishlistIds)

            delay(100)
            initialCards.update { wishlistCards }

            if (wishlistCards.isEmpty()) {
                uiState.update { WishlistScreenUiState.Empty }
            } else {
                uiState.update { WishlistScreenUiState.WishlistUi(wishlistCards) }
            }
        }
    }

    private fun getLocalUserWishlist() {
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

    private fun filterCardsByQuery() {
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

    data object Loading : WishlistScreenUiState()
    data object Empty : WishlistScreenUiState()
}