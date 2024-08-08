package com.mready.mtgtreasury.models.card

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
data class MtgCard(
    val id: String,
    val name: String,
    val prices: CardPrices,
    val releaseDate: String,
    val manaCost: String,
    val type: String,
//    the text on card that describes it s power
    val oracleText: String,
    val power: String?,
    val colors: List<String>,
    val imageUris: CardImageUris,
    val foil: Boolean,
    val edhRank: Int,
    val legalities: CardLegalities,
    val setName: String,
    val setAbbreviation: String,
    val artist: String
)

fun String.formatReleaseDate(): String {
    val customAbbreviations = mapOf(
        "January" to "Jan",
        "February" to "Feb",
        "March" to "Mar",
        "April" to "Apr",
        "May" to "May",
        "June" to "Jun",
        "July" to "Jul",
        "August" to "Aug",
        "September" to "Sept",
        "October" to "Oct",
        "November" to "Nov",
        "December" to "Dec"
    )
    val parsedDate = LocalDate.parse(this)

    val monthFullName = parsedDate.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val year = parsedDate.year
    val monthAbbreviation = customAbbreviations[monthFullName] ?: monthFullName

    return "$monthAbbreviation $year"
}

fun MtgCard.getNumberOfLegalFormats(): Int {
    val legalities = this.legalities
    val count = CardLegalities::class.members
        .filter { it.returnType.classifier == String::class }
        .count { it.call(legalities) == "legal" }
    println(count)
    return count / 2
}