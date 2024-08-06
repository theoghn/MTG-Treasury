package com.mready.mtgtreasury.models.card

data class CardLegalities(
    val standard: String,
//    val future: String,
    val historic: String,
    val timeless: String,
//    val gladiator: String,
    val pioneer: String,
    val explorer: String,
    val modern: String,
    val legacy: String,
    val pauper: String,
    val vintage: String,
    val penny: String,
    val commander: String,
    val oathBreaker: String,
//    val standardBrawl: String,
    val brawl: String,
    val alchemy: String,
//    val pauperCommander: String,
//    val duel: String,
//    val oldSchool: String,
//    val preModern: String,
//    val predh: String
)
