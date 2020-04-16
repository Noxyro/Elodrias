package de.elodrias.economy.currency

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class Currency(
        val name: String,
        var isDefault: Boolean,
        var meta: CurrencyDisplayMeta,
        val parent: Currency? = null,
        private val children: MutableSet<Currency> = mutableSetOf(),
        private val conversionTo: MutableMap<Currency, BigDecimal> = mutableMapOf(),
        private val conversionFrom: MutableMap<Currency, BigDecimal> = mutableMapOf()
) {

    private val negativePluralTreshold = BigDecimal(-2)
    private val positivePluralTreshold = BigDecimal(2)

    fun formatValue(value: BigDecimal) : String {
        val sb: StringBuilder = StringBuilder()

        val name = if (meta.isNameDisplayed) {
            if (value > negativePluralTreshold && value < positivePluralTreshold) {
                meta.nameSingular
            } else {
                meta.namePlural
            }
        } else { "" }

        if (!meta.isNameTrailing) { sb.append(name) }
        sb.append(meta.prefix)
        if (meta.isRounding) sb.append(value.round(MathContext(meta.decimals, RoundingMode.HALF_UP)))
        sb.append(meta.suffix)
        if (meta.isNameTrailing) { sb.append(name) }

        return sb.toString()
    }
}