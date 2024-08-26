package com.mready.mtgtreasury.ui.decks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.models.Deck
import com.mready.mtgtreasury.services.CardsService
import com.mready.mtgtreasury.services.DecksService
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
            decksService.getDecks().collect { decks1 ->
                decks.update { decks1 }
//                Log.d("DecksViewModel", "getDecks: ${s.size}")
            }
        }

    }

}