package de.elodrias.feature.moneydrop.listener

import de.elodrias.feature.moneydrop.MoneyDrop
import de.elodrias.feature.moneydrop.event.PlayerMoneyPickupEvent
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import kotlin.random.Random

class MoneyPickupListener(
        private val moneyDrop: MoneyDrop
) : Listener {

    @EventHandler
    fun onEntityPickupItem(event: EntityPickupItemEvent) {
        // Don't do anything when cancelled
        if (event.isCancelled) return
        // Not the item we are looking for
        if (!moneyDrop.isTaggedItemStack(event.item.itemStack)) return
        // Only players shall pick it up
        if (event.entity !is Player) { event.isCancelled = true; return }

        val amount = moneyDrop.getAmountFromItemStack(event.item.itemStack)
        val playerPickupMoneyEvent = PlayerMoneyPickupEvent(event.entity as Player, event.item, amount)
        moneyDrop.plugin.server.pluginManager.callEvent(playerPickupMoneyEvent)
        event.isCancelled = playerPickupMoneyEvent.isCancelled

        if (event.isCancelled) return
        event.isCancelled = true
        (event.entity as Player).playSound(event.item.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f + Random.nextFloat() / 2.0f)
        event.item.remove()
    }

    /*
     * TODO: The following methods are only for testing and belong into their onw features
     */

}