package com.mready.mtgtreasury.ui.decks.create

import androidx.lifecycle.ViewModel
import com.mready.mtgtreasury.services.DecksService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeckCreationViewModel @Inject constructor(private val decksService: DecksService) : ViewModel()