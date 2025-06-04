package com.theoghn.mtgtreasury.ui.search.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoghn.mtgtreasury.models.card.MtgCard
import com.theoghn.mtgtreasury.services.CardsService
import com.theoghn.mtgtreasury.services.InventoryService
import com.theoghn.mtgtreasury.utility.Constants
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
    val cardsFlow = MutableStateFlow<List<MtgCard>>(emptyList())
    val inventoryFlow = MutableStateFlow<Map<String, Int>>(emptyMap())
    val init = MutableStateFlow(false)

    init {

        viewModelScope.launch {
            getInventory()
        }
    }

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

            var cards = cardsService.getCardsByFilters(
                name = name,
                manaCost = manaCost,
                colors = colors,
                rarity = rarity,
                type = type,
                superType = superType,
            )
            cardsFlow.update { cards }

            cards = cards.map {
                it.copy(
                    qty = if (inventoryFlow.value.contains(it.id)) {
                        inventoryFlow.value[it.id]!!
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

            if (!init.value) {
                init.value = true
            }
        }
    }

    private fun getInventory() {
        viewModelScope.launch {
            inventoryService.getInventoryFlow().collect { inventory ->
                inventoryFlow.update { inventory }
                cardsFlow.update { flow ->
                    flow.map {
                        it.copy(
                            qty = if (inventory.contains(it.id)) {
                                inventory[it.id]!!
                            } else {
                                0
                            }
                        )
                    }
                }

                if (init.value) {
                    uiState.update { FilterSearchScreenUiState.FilterSearchScreenUi(cardsFlow.value) }
                }
            }
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