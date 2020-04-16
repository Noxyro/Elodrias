package de.elodrias.economy

import de.elodrias.economy.account.Account
import de.elodrias.economy.currency.Currency
import de.elodrias.economy.currency.CurrencyDisplayMeta
import de.elodrias.economy.exception.CurrencyAlreadyRegisteredException
import de.elodrias.economy.exception.CurrencyNotRegisteredException
import de.elodrias.economy.listener.EconomyListener
import de.elodrias.module.Module
import org.bukkit.plugin.Plugin

class Economy(plugin: Plugin) : Module(plugin, "economy") {

    private val accounts: MutableMap<String, Account> = mutableMapOf()
    private val currencies: MutableMap<String, Currency> = mutableMapOf()
    private var defaultCurrency: Currency? = null

    init {
        registerListener(EconomyListener(this))
        registerCurrency(Currency("gold", true, CurrencyDisplayMeta("Gold", "Gold")))
    }

    fun getCurrency(name: String) = currencies[name]

    fun getDefaultCurrency() : Currency {
        return defaultCurrency ?: currencies.values.first { it.isDefault }.also { defaultCurrency = it }
    }

    fun registerCurrency(currency: Currency) {
        currencies.putIfAbsent(currency.name, currency)?.also { throw CurrencyAlreadyRegisteredException(currency.name) }

        if (currency.isDefault && defaultCurrency != null) throw Exception("Default currency already set to ${defaultCurrency?.name}")
    }

    fun unregisterCurrency(name: String) {
        if (currencies.remove(name)?.isDefault ?: throw CurrencyNotRegisteredException(name)) defaultCurrency = null
    }

    fun unregisterCurrency(currency: Currency) {
        unregisterCurrency(currency.name)
    }

}