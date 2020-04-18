/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.features.currencydrops.listener

import de.elodrias.features.currencydrops.CurrencyDrops
import de.elodrias.features.currencydrops.event.EntityCurrencyPickupEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent

class CurrencyPickupListener(
        private val currencyDrops: CurrencyDrops
) : Listener {

    @EventHandler
    fun onEntityPickupItem(event: EntityPickupItemEvent) {
        // Don't do anything when cancelled
        if (event.isCancelled) return
        // Not the item we are looking for
        if (!currencyDrops.isTaggedItemStack(event.item.itemStack)) return

        val playerPickupMoneyEvent = EntityCurrencyPickupEvent(event.entity, event.item, currencyDrops.getAmountFromItemStack(event.item.itemStack))
        currencyDrops.plugin.server.pluginManager.callEvent(playerPickupMoneyEvent)
        event.isCancelled = playerPickupMoneyEvent.isCancelled

        if (event.isCancelled) return
        event.item.remove()
        event.isCancelled = true
    }

}