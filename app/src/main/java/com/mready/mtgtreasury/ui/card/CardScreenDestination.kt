package com.mready.mtgtreasury.ui.card

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class CardScreenDestination(val id: String, val cardImgUri: String) : Parcelable