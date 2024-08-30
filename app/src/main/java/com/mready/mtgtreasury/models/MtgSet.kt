package com.mready.mtgtreasury.models

data class MtgSet(
    val name: String,
    val iconUri: String,
    val cardCount: Int,
    val releaseDate: String,
    val scryfall_uri: String
)
