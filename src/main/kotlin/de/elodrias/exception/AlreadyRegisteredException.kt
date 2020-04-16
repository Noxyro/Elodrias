package de.elodrias.exception

import java.lang.Exception

open class AlreadyRegisteredException(
        clazz: Class<*>,
        vararg names: String
) : Exception("${clazz.simpleName} ${names.joinToString(", ", "\"", "\"")} already registered")
