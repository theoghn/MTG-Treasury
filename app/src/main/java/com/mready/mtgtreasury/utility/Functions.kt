package com.mready.mtgtreasury.utility

import android.icu.text.NumberFormat
import java.util.Locale

fun formatPrice(price: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(price)
}