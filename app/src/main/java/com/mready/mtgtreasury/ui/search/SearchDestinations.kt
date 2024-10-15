package com.mready.mtgtreasury.ui.search

import kotlinx.serialization.Serializable


@Serializable
object SearchRoot {
    @Serializable
    object SearchScreenDestination

    @Serializable
    object RecognitionScreenDestination

    @Serializable
    class FilterSearchScreenDestination(val searchName: String)
}