package de.elodrias.economy.util

import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import java.math.BigDecimal

class BigDecimalPersistentDataType : PersistentDataType<String, BigDecimal> {

    companion object {
        val INSTANCE = BigDecimalPersistentDataType()
    }

    override fun getPrimitiveType(): Class<String> = String::class.java

    override fun getComplexType(): Class<BigDecimal> = BigDecimal::class.java

    override fun toPrimitive(complex: BigDecimal, adapterContext: PersistentDataAdapterContext): String {
        return complex.toString()
    }

    override fun fromPrimitive(primitive: String, adapterContext: PersistentDataAdapterContext): BigDecimal {
        return BigDecimal(primitive)
    }

}