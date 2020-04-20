/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.economy.listener

import de.elodrias.economy.Economy
import de.elodrias.economy.account.AccountType
import de.elodrias.economy.event.AccountBalanceChangeEvent
import de.elodrias.persistence.ComplexPersistentDataType
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.persistence.PersistentDataHolder
import java.util.*

class PlayerBalanceChangeListener(
        private val economy: Economy
) : Listener {

    // TODO: This listener is only for testing purposes

    @EventHandler
    fun onAccountBalanceChange(event: AccountBalanceChangeEvent) {
        if (event.isCancelled) return
        if (event.account.type != AccountType.PLAYER) return
        val player = economy.plugin.server.getPlayer(UUID.fromString(event.account.identifier)) ?: return

        (player as PersistentDataHolder).persistentDataContainer.set(economy.createNamespacedKey("balance"), ComplexPersistentDataType.BIG_DECIMAL, event.after)
        player.sendMessage("${ChatColor.YELLOW}Your balance is now ${ChatColor.AQUA}${event.currency.formatValue(event.after)}${ChatColor.YELLOW}.")
    }
}