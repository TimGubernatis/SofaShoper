package de.syntax_institut.androidabschlussprojekt.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun formatPrice(price: Double, locale: Locale = Locale("de", "DE")): String {
    val currency = Currency.getInstance(locale)
    return NumberFormat.getCurrencyInstance(locale).apply {
        this.currency = currency
    }.format(price)
}

