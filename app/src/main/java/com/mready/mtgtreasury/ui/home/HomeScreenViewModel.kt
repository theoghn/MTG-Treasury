package com.mready.mtgtreasury.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.api.endpoints.ScryfallApi
import com.mready.mtgtreasury.models.MtgSet
import com.mready.mtgtreasury.models.card.MtgCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val api: ScryfallApi) : ViewModel() {
    val uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)

    init {
        getCard()
    }

    private fun getCard() {
        if (uiState.value == HomeScreenUiState.Loading) {
            viewModelScope.launch {
                val card = api.getCard()
                val mostValuableCards = api.getMostValuableCards()
                val newestSets = api.getNewestSets()
                uiState.update { HomeScreenUiState.HomeUi(card, mostValuableCards,newestSets) }
            }
        }
    }
}

sealed class HomeScreenUiState {
    data class HomeUi(
        val mtgCard: MtgCard?,
        val mostValuableCards: List<MtgCard>,
        val newestSets: List<MtgSet>
    ) : HomeScreenUiState()

    data object Loading : HomeScreenUiState()
}