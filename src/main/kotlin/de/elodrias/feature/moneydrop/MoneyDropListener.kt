/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.feature.moneydrop

import de.elodrias.economy.event.PlayerBalanceChangeEvent
import de.elodrias.feature.moneydrop.event.PlayerPickupMoneyEvent
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import kotlin.math.round
import kotlin.random.Random

class MoneyDropListener(
        private val moneyDrop: MoneyDrop
) : Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if (!event.entityType.isAlive) return // Only living entities drop
        if (event.droppedExp == 0) return // Only valuable entities drop

        val dropValue = moneyDrop.getDropValue(event.entityType) // Retrieve value for entity type
        if (dropValue == 0.0) return // No value

        // Randomized value ranges from half of Value to double of value
        val randomizedValue = dropValue / 2.0 + (((dropValue * 2) - (dropValue / 2)) * Random.nextDouble())
        val stack = moneyDrop.getItemStackForValue(randomizedValue) ?: return // Retrieve item stack for value
        event.drops.add(stack) // Add the items to drops
    }

    @EventHandler
    fun onEntityPickupItem(event: EntityPickupItemEvent) {
        if (event.isCancelled) return // Don't do anything when cancelled
        if (!moneyDrop.isTaggedItemStack(event.item.itemStack)) return // Not the item we are looking for
        if (event.entity !is Player) {
            event.isCancelled = true; return
        } // Only players shall pick it up
        val amount = moneyDrop.getAmountFromItemStack(event.item.itemStack)
        val playerPickupMoneyEvent = PlayerPickupMoneyEvent(event.entity as Player, event.item, amount)
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

    @EventHandler
    fun onPlayerPickupMoney(event: PlayerPickupMoneyEvent) {
        if (event.isCancelled) return
        if (event.amount <= 0.0) return // Nothing to pickup
        val previousAmount = (event.player as PersistentDataHolder).persistentDataContainer.get(NamespacedKey(moneyDrop.plugin, "economy.balance"), PersistentDataType.DOUBLE)
                ?: 0.0
        val newAmount = previousAmount + event.amount
        event.player.sendMessage("${ChatColor.GOLD}You picked up ${ChatColor.DARK_GREEN}${round(event.amount * 100) / 100}${ChatColor.GOLD} gold!")

        val playerBalanceChangeEvent = PlayerBalanceChangeEvent(event.player, previousAmount, newAmount)
        moneyDrop.plugin.server.pluginManager.callEvent(playerBalanceChangeEvent)
    }

    @EventHandler
    fun onPlayerBalanceChange(event: PlayerBalanceChangeEvent) {
        if (event.isCancelled) return
        (event.player as PersistentDataHolder).persistentDataContainer.set(NamespacedKey(moneyDrop.plugin, "economy.balance"), PersistentDataType.DOUBLE, event.to)
        event.player.sendMessage("${ChatColor.YELLOW}You balance is now ${ChatColor.AQUA}${round(event.to * 100) / 100}${ChatColor.YELLOW} gold.")
    }
}