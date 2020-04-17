/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.feature.currencydrops.listener

import de.elodrias.feature.currencydrops.CurrencyDrops
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import kotlin.random.Random

class CurrencyDropListener(
        private val currencyDrops: CurrencyDrops
) : Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        // Only living entities drop
        if (!event.entityType.isAlive) return
        // Only valuable entities drop
        if (event.droppedExp == 0) return
        // Retrieve value for entity type
        val dropValue = currencyDrops.getDropValue(event.entityType)
        // No value
        if (dropValue == 0.0) return
        // Retrieve item stack for value
        val stack = currencyDrops.getItemStackForValue(dropValue / 2.0 + (dropValue * Random.nextDouble())) ?: return



        event.drops.add(stack) // Add the items to drops
    }
}