package de.elodrias.feature.currencydrops.listener

import de.elodrias.feature.currencydrops.CurrencyDrops
import de.elodrias.feature.currencydrops.event.EntityCurrencyPickupEvent
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