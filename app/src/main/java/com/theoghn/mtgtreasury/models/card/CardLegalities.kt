package com.theoghn.mtgtreasury.models.card

data class CardLegalities(
    val standard: String,
    val historic: String,
    val timeless: String,
    val pioneer: String,
    val explorer: String,
    val modern: String,
    val legacy: String,
    val pauper: String,
    val vintage: String,
    val penny: String,
    val commander: String,
    val oathBreaker: String,
    val brawl: String,
    val alchemy: String,
)
