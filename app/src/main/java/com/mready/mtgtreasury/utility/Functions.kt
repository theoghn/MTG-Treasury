package com.mready.mtgtreasury.utility

import android.icu.text.NumberFormat
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.models.card.CardLegalities
import com.mready.mtgtreasury.models.card.MtgCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatPrice(price: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(price)
}

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

fun getProfilePictureResourceId(pictureId: Int):Int {
    return when(pictureId){
        0 -> R.drawable.profile_picture_0
        1 -> R.drawable.profile_picture_1
        2 -> R.drawable.profile_picture_2
        3 -> R.drawable.profile_picture_3
        else -> R.drawable.profile_picture_0
    }
}