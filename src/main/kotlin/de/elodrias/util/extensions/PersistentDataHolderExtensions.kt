/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.util.extensions

import de.elodrias.persistence.ComplexPersistentDataType
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import java.math.BigDecimal

// Sorted by most likely use
private val dataTypes = listOf(
        PersistentDataType.STRING,
        PersistentDataType.DOUBLE,
        PersistentDataType.INTEGER,
        PersistentDataType.LONG,
        PersistentDataType.FLOAT,
        PersistentDataType.SHORT,
        PersistentDataType.BYTE,
        PersistentDataType.INTEGER_ARRAY,
        PersistentDataType.LONG_ARRAY,
        PersistentDataType.BYTE_ARRAY,
        PersistentDataType.TAG_CONTAINER,
        ComplexPersistentDataType.BIG_DECIMAL
)

fun PersistentDataHolder.storeValue(plugin: Plugin, key: String, value: Any) {
    when (value) {
        is String -> this.persistentDataContainer.set(NamespacedKey(plugin, key), PersistentDataType.STRING, value)
        is Double -> this.persistentDataContainer.set(NamespacedKey(plugin, key), PersistentDataType.DOUBLE, value)
        is Int -> this.persistentDataContainer.set(NamespacedKey(plugin, key), PersistentDataType.INTEGER, value)
        is Float -> this.persistentDataContainer.set(NamespacedKey(plugin, key), PersistentDataType.FLOAT, value)
        is Short -> this.persistentDataContainer.set(NamespacedKey(plugin, key), PersistentDataType.SHORT, value)
        is Byte -> this.persistentDataContainer.set(NamespacedKey(plugin, key), PersistentDataType.BYTE, value)
        is Array<*> ->
            when {
                value.isArrayOf<Int>() -> this.persistentDataContainer.set(NamespacedKey(plugin, key), PersistentDataType.INTEGER_ARRAY, value as IntArray)
                value.isArrayOf<Int>() -> this.persistentDataContainer.set(NamespacedKey(plugin, key), PersistentDataType.LONG_ARRAY, value as LongArray)
                value.isArrayOf<Int>() -> this.persistentDataContainer.set(NamespacedKey(plugin, key), PersistentDataType.BYTE_ARRAY, value as ByteArray)
                else -> throw Exception("Unsupported array data type")
            }
        is PersistentDataContainer -> this.persistentDataContainer.set(NamespacedKey(plugin, key), PersistentDataType.TAG_CONTAINER, value)
        is BigDecimal -> this.persistentDataContainer.set(NamespacedKey(plugin, key), ComplexPersistentDataType.BIG_DECIMAL, value)
        else -> throw Exception("Unsupported data type")
    }
}

inline fun <reified T> PersistentDataHolder.retrieveValue(plugin: Plugin, key: String): T? {
    return when (T::class.java) {
        String::class.java -> this.persistentDataContainer.get(NamespacedKey(plugin, key), PersistentDataType.STRING) as T
        Double::class.java -> this.persistentDataContainer.get(NamespacedKey(plugin, key), PersistentDataType.DOUBLE) as T
        Int::class.java -> this.persistentDataContainer.get(NamespacedKey(plugin, key), PersistentDataType.INTEGER) as T
        Float::class.java -> this.persistentDataContainer.get(NamespacedKey(plugin, key), PersistentDataType.FLOAT) as T
        Short::class.java -> this.persistentDataContainer.get(NamespacedKey(plugin, key), PersistentDataType.SHORT) as T
        Byte::class.java -> this.persistentDataContainer.get(NamespacedKey(plugin, key), PersistentDataType.BYTE) as T
        IntArray::class.java -> this.persistentDataContainer.get(NamespacedKey(plugin, key), PersistentDataType.INTEGER_ARRAY) as T
        LongArray::class.java -> this.persistentDataContainer.get(NamespacedKey(plugin, key), PersistentDataType.LONG_ARRAY) as T
        ByteArray::class.java -> this.persistentDataContainer.get(NamespacedKey(plugin, key), PersistentDataType.BYTE_ARRAY) as T
        PersistentDataContainer::class.java -> this.persistentDataContainer.get(NamespacedKey(plugin, key), PersistentDataType.TAG_CONTAINER) as T
        BigDecimal::class.java -> this.persistentDataContainer.get(NamespacedKey(plugin, key), ComplexPersistentDataType.BIG_DECIMAL) as T
        /*KClass::class.java -> when (T::class) {
            String::class -> this.persistentDataContainer.get(NamespacedKey(plugin, key), PersistentDataType.STRING) as T
            else -> throw Exception("Unsupported kotlin data type")
        }*/
        else -> throw Exception("Unsupported data type")
    }
}

fun PersistentDataHolder.hasValue(plugin: Plugin, key: String): Boolean {
    return this.persistentDataContainer.has(NamespacedKey(plugin, key), findType(this, NamespacedKey(plugin, key))
            ?: return false)
}

private fun findType(holder: PersistentDataHolder, namespacedKey: NamespacedKey): PersistentDataType<*, *>? {
    return dataTypes.filter { holder.persistentDataContainer.has(namespacedKey, it) }.let {
        if (it.size > 1) throw Exception("Found more than one types applicable for key $namespacedKey")
        it.firstOrNull()
    }
}