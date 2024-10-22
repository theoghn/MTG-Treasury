package com.theoghn.mtgtreasury.models.card

data class CardPrices(
    val usd: String,
    val usdFoil: String,
    val usdEtched: String,
    val eur: String,
    val eurFoil: String,
    val tix: String
)