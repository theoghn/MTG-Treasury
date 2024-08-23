package com.mready.mtgtreasury.ui.decks

import androidx.lifecycle.ViewModel
import com.mready.mtgtreasury.services.CardsService
import com.mready.mtgtreasury.services.DecksService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DecksViewModel @Inject constructor(private val decksService: DecksService) : ViewModel()