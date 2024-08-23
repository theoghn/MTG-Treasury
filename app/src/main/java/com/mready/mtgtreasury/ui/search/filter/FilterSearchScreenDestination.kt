package com.mready.mtgtreasury.ui.search.filter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class FilterSearchScreenDestination(val searchName: String?) : Parcelable