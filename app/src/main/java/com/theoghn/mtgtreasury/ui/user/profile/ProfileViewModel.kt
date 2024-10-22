package com.theoghn.mtgtreasury.ui.user.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoghn.mtgtreasury.models.AppUser
import com.theoghn.mtgtreasury.models.Deck
import com.theoghn.mtgtreasury.models.card.MtgCard
import com.theoghn.mtgtreasury.services.CardsService
import com.theoghn.mtgtreasury.services.DecksService
import com.theoghn.mtgtreasury.services.ExternalUserService
import com.theoghn.mtgtreasury.services.InventoryService
import com.theoghn.mtgtreasury.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val cardsService: CardsService,
    private val inventoryService: InventoryService,
    private val userService: UserService,
    private val decksService: DecksService,
    private val externalUserService: ExternalUserService
) : ViewModel() {
    val uiState = MutableStateFlow<ProfileScreenUiState>(ProfileScreenUiState.Loading)

    fun initialize(userId: String) {
        if (uiState.value == ProfileScreenUiState.Loading) {
            when (userId) {
                userService.getUID() -> getLocalUserInfo()
                else -> getForeignUserInfo(userId)
            }
        }
    }

    private fun getLocalUserInfo() {
        viewModelScope.launch {
            inventoryService.getInventoryFlow()
            val combinedFlow = decksService.getDecksFlow()
                .combine(userService.getUserFlow()) { decks, user ->
                    Pair(decks, user)
                }
            combinedFlow.collect { flow ->
                val decks = flow.first
                val user = flow.second

                if (user != null) {
                    val inventoryCards = cardsService.getCardsByIds(user.inventory.keys.toList().take(10))
                    val wishlistCards = cardsService.getCardsByIds(user.wishlist.take(10))

                    uiState.update {
                        ProfileScreenUiState.ProfileUi(user, inventoryCards, wishlistCards, decks,true)
                    }
                }
            }
        }
    }

    private fun getForeignUserInfo(userId: String) {
        viewModelScope.launch {
            val user = externalUserService.getUserInfo(userId)
            val decks = externalUserService.getUserDecks(userId)
            val inventoryCards = cardsService.getCardsByIds(user.inventory.keys.toList().take(10))
            val wishlistCards = cardsService.getCardsByIds(user.wishlist.take(10))

            uiState.update {
                ProfileScreenUiState.ProfileUi(user, inventoryCards, wishlistCards, decks, false)
            }
        }
    }
}

sealed class ProfileScreenUiState {
    data class ProfileUi(
        val user: AppUser,
        val inventoryCards: List<MtgCard>,
        val wishlistCards: List<MtgCard>,
        val decks: List<Deck>,
        val isLocalUser: Boolean
    ) : ProfileScreenUiState()

    data object Loading : ProfileScreenUiState()
}