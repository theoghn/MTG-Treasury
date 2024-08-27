package com.mready.mtgtreasury.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.models.AppUser
import com.mready.mtgtreasury.models.MtgSet
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.services.CardsService
import com.mready.mtgtreasury.services.InventoryService
import com.mready.mtgtreasury.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val cardsService: CardsService,
    private val inventoryService: InventoryService,
    private val userService: UserService
) : ViewModel() {
    val uiState = MutableStateFlow<ProfileScreenUiState>(ProfileScreenUiState.Loading)

    init {
        viewModelScope.launch {
            inventoryService.getInventoryFlow()
            userService.getUserFlow().collect { user ->
                if (user != null) {
                    val inventoryCards = cardsService.getCardsByIds(user.inventory.keys.toList())
                    val wishlistCards = cardsService.getCardsByIds(user.wishlist)

                    uiState.update {
                        ProfileScreenUiState.ProfileUi(user, inventoryCards, wishlistCards)
                    }
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userService.signOut()
        }
    }
}

sealed class ProfileScreenUiState {
    data class ProfileUi(
        val user: AppUser,
        val inventoryCards: List<MtgCard>,
        val wishlistCards: List<MtgCard>
    ) : ProfileScreenUiState()

    data object Loading : ProfileScreenUiState()
}