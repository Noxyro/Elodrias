/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.feature.moneydrop.listener

import de.elodrias.economy.event.PlayerBalanceChangeEvent
import de.elodrias.feature.moneydrop.MoneyDrop
import de.elodrias.feature.moneydrop.event.PlayerMoneyPickupEvent
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import kotlin.math.round
import kotlin.random.Random

class MoneyDropListener(
        private val moneyDrop: MoneyDrop
) : Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        // Only living entities drop
        if (!event.entityType.isAlive) return
        // Only valuable entities drop
        if (event.droppedExp == 0) return
        // Retrieve value for entity type
        val dropValue = moneyDrop.getDropValue(event.entityType)
        // No value
        if (dropValue == 0.0) return

        // Randomized value ranges from half of Value to double of value
        val randomizedValue = dropValue / 2.0 + (dropValue * Random.nextDouble())
        val stack = moneyDrop.getItemStackForValue(randomizedValue) ?: return // Retrieve item stack for value
        event.drops.add(stack) // Add the items to drops
    }
}