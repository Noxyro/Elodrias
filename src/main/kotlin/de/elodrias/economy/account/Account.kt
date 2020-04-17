package de.elodrias.economy.account

import de.elodrias.economy.Economy
import de.elodrias.economy.currency.Currency
import de.elodrias.economy.event.AccountBalanceChangeEvent
import java.math.BigDecimal

data class Account(
        val economy: Economy,
        val type: AccountType,
        val identifier: String,
        private val balances: MutableMap<Currency, BigDecimal> = mutableMapOf()
) {
    companion object {
        fun createTypedIdentifier(type: AccountType, identifier: String): String {
            return "${type.name.toLowerCase()}:${identifier}"
        }
    }

    fun getTypedIdentifier(): String {
        return createTypedIdentifier(type, identifier)
    }

    fun getBalance(currency: Currency): BigDecimal {
        return balances.getOrDefault(currency, BigDecimal.ZERO)
    }

    fun changeBalance(currency: Currency, value: BigDecimal): Boolean {
        val current = balances.getOrDefault(currency, BigDecimal.ZERO)
        val event = AccountBalanceChangeEvent(this, currency, current, value, current.add(value))
        economy.plugin.server.pluginManager.callEvent(event)
        if (event.isCancelled) return false
        (balances[currency]?.plus(value) ?: value).also { balances[currency] = it }
        return true
    }

    fun changeBalance(currency: Currency, value: Double): Boolean {
        return changeBalance(currency, BigDecimal(value))
    }
}