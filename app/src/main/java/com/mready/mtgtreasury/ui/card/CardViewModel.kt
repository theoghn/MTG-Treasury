package com.mready.mtgtreasury.ui.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.api.endpoints.ScryfallApi
import com.mready.mtgtreasury.models.card.MtgCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CardViewModel @Inject constructor(private val api: ScryfallApi) : ViewModel() {
    val uiState = MutableStateFlow<CardScreenUiState>(CardScreenUiState.Loading)

    fun getCard(id: String) {
        if (uiState.value == CardScreenUiState.Loading) {
            viewModelScope.launch {
                val card = api.getCard(id)
                uiState.update { CardScreenUiState.CardUi(card) }
            }
        }
    }
}

sealed class CardScreenUiState {
    data class CardUi(
        val mtgCard: MtgCard?
    ) : CardScreenUiState()

    data object Loading : CardScreenUiState()
}