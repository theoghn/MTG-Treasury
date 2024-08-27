package com.mready.mtgtreasury.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class HomeScreenViewModel @Inject constructor(
    private val cardsService: CardsService,
    private val userService: UserService,
    private val inventoryService: InventoryService
) : ViewModel() {
    val uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            val card = cardsService.getRandomCard()
            val mostValuableCards = cardsService.getMostValuableCards()
            val newestSets = cardsService.getNewestSets()

            inventoryService.getInventoryFlow().collect {
                val inventoryValue = inventoryService.calculateInventoryValue(it)
                userService.updateInventoryValue(inventoryValue)
                uiState.update {
                    HomeScreenUiState.HomeUi(
                        card,
                        mostValuableCards,
                        newestSets,
                        inventoryValue
                    )
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