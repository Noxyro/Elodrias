package de.elodrias.economy.exception

import de.elodrias.economy.currency.Currency
import de.elodrias.exception.AlreadyRegisteredException

class CurrencyAlreadyRegisteredException(name: String) : AlreadyRegisteredException(Currency::class.java, name)