package com.mready.mtgtreasury.ui.search

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.services.CardsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(private val api: CardsService) : ViewModel() {
    var searchQuery = MutableStateFlow(TextFieldValue())

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<String>> =
        searchQuery.asStateFlow()
            .mapLatest { query ->
                if (query.text.isEmpty()) {
                    emptyList()
                } else {
                    try {
                        val results = api.getCardSuggestions(query.text)
                        results
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
            }.debounce(300).stateIn(
                scope = viewModelScope,
                initialValue = emptyList(),
                started = SharingStarted.WhileSubscribed(5000)
            )

    fun onSearchQueryChange(newQuery: TextFieldValue) {
        searchQuery.update { newQuery }
    }
}


