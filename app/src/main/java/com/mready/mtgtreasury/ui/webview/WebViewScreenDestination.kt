package com.mready.mtgtreasury.ui.webview

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable

data class WebViewScreenDestination(val url: String) : Parcelable