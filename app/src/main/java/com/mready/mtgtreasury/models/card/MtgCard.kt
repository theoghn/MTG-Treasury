package com.mready.mtgtreasury.models.card

data class MtgCard(
    val id: String,
    val name: String,
    val prices: CardPrices,
    val releaseDate: String,
    val manaCost: String,
    val type: String,
    val oracleText: String,
    val power: String?,
    val colors: List<String>,
    val imageUris: CardImageUris,
    val foil: Boolean,
    val edhRank: Int,
    val legalities: CardLegalities,
    val setName: String,
    val setAbbreviation: String,
    val artist: String,
    val isFavorite : Boolean = false,
    val qty : Int = 0
)

