package de.elodrias.economy.listener

import de.elodrias.economy.Economy
import de.elodrias.economy.event.PlayerBalanceChangeEvent
import de.elodrias.economy.util.BigDecimalPersistentDataType
import de.elodrias.feature.moneydrop.event.PlayerMoneyPickupEvent
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.persistence.PersistentDataHolder
import java.math.BigDecimal
import kotlin.math.round

class PlayerMoneyPickupListener(
    private val economy: Economy
) {

    @EventHandler
    fun onPlayerMoneyPickup(event: PlayerMoneyPickupEvent) {
        if (event.isCancelled) return
        if (event.amount <= 0.0) return // Nothing to pickup
        // TODO: Replace with real money saving
        val previousAmount = (event.player as PersistentDataHolder).persistentDataContainer.get(economy.createNamespacedKey("balance"), BigDecimalPersistentDataType.INSTANCE)
                ?: BigDecimal.ZERO
        val newAmount = previousAmount + BigDecimal(event.amount)
        event.player.sendMessage("${ChatColor.GOLD}You picked up ${ChatColor.DARK_GREEN}${round(event.amount * 100) / 100}${ChatColor.GOLD} gold!")

        val playerBalanceChangeEvent = PlayerBalanceChangeEvent(event.player, economy.getDefaultCurrency(), previousAmount, newAmount)
        economy.plugin.server.pluginManager.callEvent(playerBalanceChangeEvent)
    }
}