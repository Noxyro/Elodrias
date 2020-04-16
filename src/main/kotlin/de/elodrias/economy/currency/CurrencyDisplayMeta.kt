package de.elodrias.economy.currency

import java.math.BigDecimal

data class CurrencyDisplayMeta(
        var nameSingular: String,
        var namePlural: String,
        var decimals: Int = 2,
        var prefix: String = "",
        var suffix: String = "",
        var isRounding: Boolean = true,
        var isNameTrailing: Boolean = true,
        var isNameDisplayed: Boolean = true
)