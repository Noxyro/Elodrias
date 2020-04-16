package de.elodrias.exception

import java.lang.Exception

open class NotRegisteredException(
        clazz: Class<*>,
        vararg names: String
) : Exception("${clazz.simpleName} ${names.joinToString(", ", "\"", "\"")} not registered")
