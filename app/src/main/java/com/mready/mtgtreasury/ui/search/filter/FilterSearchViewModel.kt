package com.mready.mtgtreasury.ui.search.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.services.CardsService
import com.mready.mtgtreasury.services.InventoryService
import com.mready.mtgtreasury.utility.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FilterSearchViewModel @Inject constructor(
    private val cardsService: CardsService,
    private val inventoryService: InventoryService
) : ViewModel() {
    val manaCosts = MutableStateFlow<Map<String, String>>(emptyMap())
    val uiState = MutableStateFlow<FilterSearchScreenUiState>(FilterSearchScreenUiState.Loading)
    val init = MutableStateFlow(false)

    fun getCosts() {
        viewModelScope.launch {
            delay(300)
            manaCosts.update { Constants.SearchFilterValues.MANA_COST }
        }
    }

    fun searchCards(
        name: String,
        manaCost: List<String>,
        colors: List<String>,
        rarity: List<String>,
        type: List<String>,
        superType: List<String>,
    ) {
        viewModelScope.launch {
            uiState.update { FilterSearchScreenUiState.Loading }

            inventoryService.getInventoryFlow().collect { inventory ->
                var cards = cardsService.getCardsByFilters(
                    name = name,
                    manaCost = manaCost,
                    colors = colors,
                    rarity = rarity,
                    type = type,
                    superType = superType,
                )

                cards = cards.map {
                    it.copy(
                        qty = if (inventory.contains(it.id)) {
                            inventory[it.id]!!
                        } else {
                            0
                        }
                    )
                }

                delay(100)

                if (cards.isEmpty()) {
                    uiState.update { FilterSearchScreenUiState.Empty }
                } else {
                    uiState.update { FilterSearchScreenUiState.FilterSearchScreenUi(cards) }
                }
            }
        }
        if (!init.value) {
            init.value = true
        }
    }

    fun addCardToInventory(cardId: String) {
        viewModelScope.launch {
            inventoryService.addCardToInventory(cardId)
        }
    }
}

sealed class FilterSearchScreenUiState {
    data class FilterSearchScreenUi(
        val cards: List<MtgCard>,
    ) : FilterSearchScreenUiState()

    data object Loading : FilterSearchScreenUiState()
    data object Empty : FilterSearchScreenUiState()
}