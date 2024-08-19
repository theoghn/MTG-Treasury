package com.mready.mtgtreasury.ui.search.filter

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.models.MtgSet
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.services.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FilterSearchViewModel @Inject constructor(private val api: ApiService) : ViewModel() {
    val manaCosts = MutableStateFlow<Map<String, String>>(emptyMap())
    val cards = MutableStateFlow<List<MtgCard>>(emptyList())

    fun getCosts() {
        viewModelScope.launch {
            delay(300)
            manaCosts.update { SearchFilterValues.MANA_COST }
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
            cards.update{
                api.getCardsByFilters(
                    name = name,
                    manaCost = manaCost,
                    colors = colors,
                    rarity = rarity,
                    type = type,
                    superType = superType,
                )
            }
        }
    }
}

sealed class FilterSearchScreenUiState {
    data class HomeUi(
        val cards: List<MtgCard>,
    ) : FilterSearchScreenUiState()

    data object Loading : FilterSearchScreenUiState()
}