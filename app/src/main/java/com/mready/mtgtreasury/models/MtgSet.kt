package com.mready.mtgtreasury.models

data class MtgSet(
    val name: String,
    val iconUri: String,
    val cardCount: Int,
    val releaseDate: String,
    val infoUri: String
)
