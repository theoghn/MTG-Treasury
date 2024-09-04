package com.mready.mtgtreasury.ui.decks.view

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class DeckScreenDestination(val id: String) : Parcelable