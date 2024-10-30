package com.theoghn.mtgtreasury.ui.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoghn.mtgtreasury.models.Deck
import com.theoghn.mtgtreasury.services.DecksService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DecksViewModel @Inject constructor(private val decksService: DecksService) : ViewModel() {

    val decks = MutableStateFlow<List<Deck>>(emptyList())

    init {
        viewModelScope.launch {
            decksService.getDecksFlow().collect { decks1 ->
                decks.update { decks1 }
            }
        }
    }
}