package de.elodrias.economy.account

import de.elodrias.economy.currency.Currency
import java.math.BigDecimal

data class Account(
        val type: AccountType,
        val identifier: AccountIdentifier,
        private val balances: MutableMap<Currency, BigDecimal>
) {
    companion object {
        fun getTypedIdentifier(type: AccountType, identifier: AccountIdentifier) : String {
            return "${type.name.toLowerCase()}.${identifier.namespace}.${identifier.key}"
        }
    }

    fun changeBalance(currency: Currency, change: BigDecimal) : BigDecimal {
        return (balances[currency]?.plus(change) ?: change).also { balances[currency] = it }
    }
}