package com.mready.mtgtreasury.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.services.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(private val api: ApiService) : ViewModel() {
    var searchQuery = MutableStateFlow("")
//    val stringsFlow = MutableStateFlow<List<String>>(emptyList())

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<String>> =
        searchQuery.asStateFlow()
            .debounce(300)
            .flatMapLatest { query ->
                flow {
                    if (query.isEmpty()) {
                        emit(emptyList())
                    } else {
                        try {
                            val results = api.getCardSuggestions(query)
//                            stringsFlow.update { results }
                            emit(results)
                        } catch (e: Exception) {
                            emit(emptyList())
                        }
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                initialValue = emptyList(),
                started = SharingStarted.WhileSubscribed(5_000)
            )

    fun onSearchQueryChange(newQuery: String) {
        searchQuery.update { newQuery }
    }
}


