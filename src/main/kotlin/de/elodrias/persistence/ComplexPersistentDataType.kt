/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.persistence

import de.elodrias.economy.util.BigDecimalPersistentDataType
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType

abstract class ComplexPersistentDataType<T> : PersistentDataType<String, T> {

    companion object {
        val BIG_DECIMAL = BigDecimalPersistentDataType.INSTANCE
    }

    override fun getPrimitiveType(): Class<String> = String::class.java

    override fun toPrimitive(complex: T, adapterContext: PersistentDataAdapterContext): String {
        return complex.toString()
    }

}