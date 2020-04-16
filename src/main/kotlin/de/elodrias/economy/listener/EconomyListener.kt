package de.elodrias.economy.listener

import de.elodrias.economy.Economy
import de.elodrias.economy.event.PlayerBalanceChangeEvent
import de.elodrias.economy.util.BigDecimalPersistentDataType
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.persistence.PersistentDataHolder

class EconomyListener(
    private val economy: Economy
) : Listener {

    @EventHandler
    fun onPlayerBalanceChange(event: PlayerBalanceChangeEvent) {
        if (event.isCancelled) return
        (event.player as PersistentDataHolder).persistentDataContainer.set(economy.createNamespacedKey("balance"), BigDecimalPersistentDataType.INSTANCE, event.to)
        event.player.sendMessage("${ChatColor.YELLOW}You balance is now ${ChatColor.AQUA}${event.currency.formatValue(event.to)}${ChatColor.YELLOW}.")
    }
}