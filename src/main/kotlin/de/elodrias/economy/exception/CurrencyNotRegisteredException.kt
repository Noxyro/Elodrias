package de.elodrias.economy.exception

import de.elodrias.economy.currency.Currency
import de.elodrias.exception.NotRegisteredException

class CurrencyNotRegisteredException(name: String) : NotRegisteredException(Currency::class.java, name)