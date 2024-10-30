package com.theoghn.mtgtreasury.ui.cardslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoghn.mtgtreasury.models.card.MtgCard
import com.theoghn.mtgtreasury.services.CardsService
import com.theoghn.mtgtreasury.services.ExternalUserService
import com.theoghn.mtgtreasury.services.InventoryService
import com.theoghn.mtgtreasury.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val cardsService: CardsService,
    private val inventoryService: InventoryService,
    private val userService: UserService,
    private val externalUserService: ExternalUserService
) : ViewModel() {
    private val initialCards = MutableStateFlow(emptyList<MtgCard>())
    val uiState = MutableStateFlow<InventoryScreenUiState>(InventoryScreenUiState.Loading)
    var searchQuery = MutableStateFlow("")

    fun initialize(userId: String) {
        if (uiState.value == InventoryScreenUiState.Loading) {
            when (userId) {
                userService.getUID() -> getLocalUserInventory()
                else -> getForeignUserInventory(userId)
            }
        }
    }

    private fun getForeignUserInventory(userId: String){
        viewModelScope.launch {
            val inventory = externalUserService.getUserInventory(userId)
            var inventoryCards = cardsService.getCardsByIds(inventory.keys.toList())

            inventoryCards = inventoryCards.map {
                it.copy(
                    qty = if (inventory.contains(it.id)) {
                        inventory[it.id]!!
                    } else {
                        0
                    }
                )
            }

            initialCards.update { inventoryCards }

            if (inventoryCards.isEmpty()) {
                uiState.update { InventoryScreenUiState.Empty }
            } else {
                uiState.update { InventoryScreenUiState.InventoryUi(inventoryCards) }
            }
        }
    }

    private fun getLocalUserInventory() {
        viewModelScope.launch {
            delay(400)

            inventoryService.getInventoryFlow().collect { inventory ->
                var inventoryCards = cardsService.getCardsByIds(inventory.keys.toList())

                inventoryCards = inventoryCards.map {
                    it.copy(
                        qty = if (inventory.contains(it.id)) {
                            inventory[it.id]!!
                        } else {
                            0
                        }
                    )
                }

                initialCards.update { inventoryCards }

                if (inventoryCards.isEmpty()) {
                    uiState.update { InventoryScreenUiState.Empty }
                } else {
                    uiState.update { InventoryScreenUiState.InventoryUi(inventoryCards) }
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
            uiState.update { InventoryScreenUiState.Empty }
        } else {
            uiState.update { InventoryScreenUiState.InventoryUi(filteredCards) }
        }
    }

}

sealed class InventoryScreenUiState {
    data class InventoryUi(
        val cards: List<MtgCard>,
    ) : InventoryScreenUiState()

    data object Loading : InventoryScreenUiState()
    data object Empty : InventoryScreenUiState()
}