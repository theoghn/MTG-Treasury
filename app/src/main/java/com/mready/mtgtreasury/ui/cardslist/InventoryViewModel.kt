package com.mready.mtgtreasury.ui.cardslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.services.CardsService
import com.mready.mtgtreasury.services.InventoryService
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
class InventoryViewModel @Inject constructor(
    private val cardsService: CardsService,
    private val inventoryService: InventoryService
) : ViewModel() {
    val initialCards = MutableStateFlow(emptyList<MtgCard>())
    val uiState = MutableStateFlow<InventoryScreenUiState>(InventoryScreenUiState.Loading)
    var searchQuery = MutableStateFlow("")

    init {
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

    fun filterCardsByQuery() {
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

    object Loading : InventoryScreenUiState()
    object Empty : InventoryScreenUiState()
}