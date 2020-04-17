/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.feature.currencydrops

import de.elodrias.economy.Economy
import de.elodrias.feature.currencydrops.listener.CurrencyDropListener
import de.elodrias.feature.currencydrops.listener.CurrencyPickupListener
import de.elodrias.feature.currencydrops.listener.EntityCurrencyPickupListener
import de.elodrias.module.Module
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

class CurrencyDrops(
        plugin: Plugin,
        private val economy: Economy
) : Module(plugin, CurrencyDrops::class.java) {

    private val dropValues = mapOf(
            EntityType.BAT to 0.1,
            EntityType.BEE to 0.1,
            EntityType.BLAZE to 4.0,
            EntityType.CAT to 0.2,
            EntityType.CAVE_SPIDER to 0.5,
            EntityType.CHICKEN to 0.3,
            EntityType.COD to 0.1,
            EntityType.COW to 0.4,
            EntityType.CREEPER to 2.0,
            EntityType.DOLPHIN to 0.5,
            EntityType.DONKEY to 0.5,
            EntityType.DROWNED to 1.0,
            EntityType.ELDER_GUARDIAN to 10.0,
            EntityType.ENDERMAN to 5.0,
            EntityType.ENDERMITE to 0.5,
            EntityType.ENDER_DRAGON to 100.0,
            EntityType.EVOKER to 3.0,
            EntityType.FOX to 100.0,
            EntityType.GHAST to 10.0,
            EntityType.GIANT to 50.0,
            EntityType.GUARDIAN to 5.0,
            EntityType.HORSE to 0.5,
            EntityType.HUSK to 1.0,
            EntityType.ILLUSIONER to 3.0,
            EntityType.IRON_GOLEM to 5.0,
            EntityType.LLAMA to 0.5,
            EntityType.MAGMA_CUBE to 5.0,
            EntityType.MULE to 0.5,
            EntityType.MUSHROOM_COW to 0.5,
            EntityType.OCELOT to 0.5,
            EntityType.PANDA to 0.5,
            EntityType.PARROT to 0.5,
            EntityType.PHANTOM to 4.0,
            EntityType.PIG to 0.5,
            EntityType.PIG_ZOMBIE to 2.0,
            EntityType.PILLAGER to 3.0,
            EntityType.POLAR_BEAR to 1.0,
            EntityType.PUFFERFISH to 0.1,
            EntityType.RABBIT to 0.1,
            EntityType.RAVAGER to 3.0,
            EntityType.SALMON to 0.1,
            EntityType.SHEEP to 0.5,
            EntityType.SHULKER to 10.0,
            EntityType.SILVERFISH to 0.5,
            EntityType.SKELETON to 1.5,
            EntityType.SKELETON_HORSE to 0.5,
            EntityType.SLIME to 0.1,
            EntityType.SNOWMAN to 0.1,
            EntityType.SPIDER to 1.0,
            EntityType.SQUID to 0.1,
            EntityType.STRAY to 1.0,
            EntityType.TRADER_LLAMA to 0.5,
            EntityType.TROPICAL_FISH to 0.1,
            EntityType.TURTLE to 0.5,
            EntityType.VEX to 1.0,
            EntityType.VILLAGER to 1.0,
            EntityType.VINDICATOR to 3.0,
            EntityType.WANDERING_TRADER to 1.0,
            EntityType.WITCH to 2.0,
            EntityType.WITHER to 50.0,
            EntityType.WITHER_SKELETON to 5.0,
            EntityType.WOLF to 0.5,
            EntityType.ZOMBIE to 1.0,
            EntityType.ZOMBIE_HORSE to 0.5,
            EntityType.ZOMBIE_VILLAGER to 1.0
    )

    private val rangesToItemStack = mapOf(
            0.0..1.0 to (ItemStack(Material.GOLD_NUGGET) to 64.0),
            1.0..64.0 to (ItemStack(Material.GOLD_INGOT) to 1.0),
            64.0..Double.POSITIVE_INFINITY to (ItemStack(Material.GOLD_BLOCK) to 0.015625) // 1 / 64
    )

    private val amountKey = createNamespacedKey("amount")

    override fun init() {
        registerListener(CurrencyDropListener(this))
        registerListener(CurrencyPickupListener(this))
        registerListener(EntityCurrencyPickupListener(economy))
    }

    fun getDropValue(type: EntityType): Double {
        return dropValues[type] ?: 0.0
    }

    fun getItemStackForValue(value: Double): ItemStack? {
        if (value <= 0.0) return null

        return rangesToItemStack.entries.first {
            value >= it.key.start && value < it.key.endInclusive
        }.value.let {
            it.first.clone().apply {
                amount = this.maxStackSize.coerceAtMost((value * it.second).toInt())
                (this.itemMeta as PersistentDataHolder).let { holder ->
                    holder.persistentDataContainer.set(amountKey, PersistentDataType.DOUBLE, value)
                    this.itemMeta = holder as ItemMeta
                }
            }
        }
    }

    fun isTaggedItemStack(stack: ItemStack): Boolean {
        if (!stack.hasItemMeta()) return false
        return (stack.itemMeta as PersistentDataHolder).persistentDataContainer.has(amountKey, PersistentDataType.DOUBLE)
    }

    fun getAmountFromItemStack(stack: ItemStack): Double {
        if (!stack.hasItemMeta()) return 0.0
        return (stack.itemMeta as PersistentDataHolder).persistentDataContainer.get(amountKey, PersistentDataType.DOUBLE)
                ?: 0.0
    }

}