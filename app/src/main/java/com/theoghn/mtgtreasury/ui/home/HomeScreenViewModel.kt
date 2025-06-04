package com.theoghn.mtgtreasury.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoghn.mtgtreasury.models.MtgSet
import com.theoghn.mtgtreasury.models.card.MtgCard
import com.theoghn.mtgtreasury.services.CardsService
import com.theoghn.mtgtreasury.services.InventoryService
import com.theoghn.mtgtreasury.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val cardsService: CardsService,
    private val userService: UserService,
    private val inventoryService: InventoryService
) : ViewModel() {
    val uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)

    init {
        viewModelScope.launch {
            try {
                val card = cardsService.getRandomCard()
                val mostValuableCards = cardsService.getMostValuableCards()
                val newestSets = cardsService.getNewestSets()
                val inventory = inventoryService.getInventory()
                val inventoryValue = inventoryService.calculateInventoryValue(inventory)
                userService.updateInventoryValue(inventoryValue)
                uiState.update {
                    HomeScreenUiState.HomeUi(
                        card,
                        mostValuableCards,
                        newestSets,
                        inventoryValue
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "initialize: ${e.message}")
            }
        }
    }

    fun updateInventoryValue() {
        viewModelScope.launch {
            val inventory = inventoryService.getInventory()
            val inventoryValue = inventoryService.calculateInventoryValue(inventory)
            userService.updateInventoryValue(inventoryValue)
            if( uiState.value is HomeScreenUiState.HomeUi) {
                val currentState = uiState.value as HomeScreenUiState.HomeUi
                uiState.update {
                    currentState.copy(inventoryValue = inventoryValue)
                }
            }
        }
    }
}

sealed class HomeScreenUiState {
    data class HomeUi(
        val mtgCard: MtgCard,
        val mostValuableCards: List<MtgCard>,
        val newestSets: List<MtgSet>,
        val inventoryValue: Double
    ) : HomeScreenUiState()

    data object Loading : HomeScreenUiState()
}