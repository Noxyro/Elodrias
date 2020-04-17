package de.elodrias.economy

import de.elodrias.economy.account.Account
import de.elodrias.economy.account.AccountType
import de.elodrias.economy.currency.Currency
import de.elodrias.economy.currency.CurrencyDisplayMeta
import de.elodrias.economy.exception.CurrencyAlreadyRegisteredException
import de.elodrias.economy.exception.CurrencyNotRegisteredException
import de.elodrias.economy.listener.PlayerBalanceChangeListener
import de.elodrias.module.Module
import org.bukkit.plugin.Plugin

class Economy(plugin: Plugin) : Module(plugin, Economy::class.java) {

    private val accounts: MutableMap<String, Account> = mutableMapOf()
    private val currencies: MutableMap<String, Currency> = mutableMapOf()
    private var defaultCurrency: Currency? = null

    init {
        registerListener(PlayerBalanceChangeListener(this))
        registerCurrency(Currency("gold", true, CurrencyDisplayMeta("Gold", "Gold", 2, "", " ")))
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

    fun getAccount(type: AccountType, id: String): Account? {
        return accounts[Account.createTypedIdentifier(type, id)]
    }

    fun getOrCreateAccount(type: AccountType, id: String): Account {
        return accounts.getOrPut(Account.createTypedIdentifier(type, id)) { Account(this, type, id) }
    }

}