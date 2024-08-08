package com.mready.mtgtreasury.ui.card

import android.os.Parcelable
import com.mready.mtgtreasury.models.card.MtgCard
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlinx.serialization.Serializable

@Serializable
data class CardScreenDestination(val id: String)