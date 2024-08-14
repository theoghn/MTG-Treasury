package com.mready.mtgtreasury.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.services.ApiService
import com.mready.mtgtreasury.ui.home.HomeScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(private val api: ApiService) : ViewModel() {
    val manaCosts = MutableStateFlow<Map<String,String>>(emptyMap())

    fun getCosts() {
        viewModelScope.launch {
            delay(300)
            manaCosts.update { SearchFilterValues.MANA_COST }
        }
    }
}


