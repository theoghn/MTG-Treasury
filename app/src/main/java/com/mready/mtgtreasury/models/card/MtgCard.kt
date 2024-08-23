package com.mready.mtgtreasury.models.card

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

import java.time.format.DateTimeFormatter
import kotlin.random.*


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
    val isInInventory : Boolean = false
)

fun String.formatReleaseDate(): String {
    val parsedDate = LocalDate.parse(this)
    val customFormat = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH)
    val formattedDate = parsedDate.format(customFormat)
    return formattedDate
}

fun MtgCard.getNumberOfLegalFormats(): Int {
    val legalities = this.legalities
    val count = CardLegalities::class.members
        .filter { it.returnType.classifier == String::class }
        .count { it.call(legalities) == "legal" }
    return count / 2
}