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

import de.elodrias.economy.Economy
import de.elodrias.economy.account.AccountType
import de.elodrias.features.currencydrops.event.EntityCurrencyPickupEvent
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.math.BigDecimal
import kotlin.random.Random

class EntityCurrencyPickupListener(
        private val economy: Economy
) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityCurrencyPickup(event: EntityCurrencyPickupEvent) {
        // Already cancelled
        if (event.isCancelled) return
        // Nothing to pickup
        if (event.amount <= 0.0) return
        // Only players shall pick it up
        if (event.entity !is Player) {
            event.isCancelled = true; return
        }

        val player = event.entity
        val account = economy.getOrCreateAccount(AccountType.PLAYER, player.uniqueId.toString())
        account.changeBalance(economy.getDefaultCurrency(), BigDecimal(event.amount))
        player.sendMessage("${ChatColor.GOLD}You picked up ${ChatColor.DARK_GREEN}${economy.getDefaultCurrency().formatValue(BigDecimal.valueOf(event.amount))}${ChatColor.GOLD}!")
        player.playSound(event.item.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f + Random.nextFloat() / 2.0f)
    }
}