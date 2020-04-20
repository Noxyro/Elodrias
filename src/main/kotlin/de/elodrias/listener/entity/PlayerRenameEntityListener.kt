/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.listener.entity

import de.elodrias.event.entity.EntityRenameEvent
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin

class PlayerRenameEntityListener(
        private val plugin: Plugin
) : Listener {

    @EventHandler
    fun onPlayerInteractAtEntity(event: PlayerInteractAtEntityEvent) {
        handleEvent(event, event.player, event.hand, event.rightClicked)
    }

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        handleEvent(event, event.player, event.hand, event.rightClicked)
    }

    private fun handleEvent(event: Cancellable, player: Player, hand: EquipmentSlot, entity: Entity) {
        val itemStack = if (hand == EquipmentSlot.HAND) player.inventory.itemInMainHand else if (hand == EquipmentSlot.OFF_HAND) player.inventory.itemInOffHand else return
        if (itemStack.type != Material.NAME_TAG) return

        val renameEvent = EntityRenameEvent(entity, player, entity.customName, itemStack.itemMeta!!.displayName)
        plugin.server.pluginManager.callEvent(renameEvent)
        event.isCancelled = renameEvent.isCancelled
    }
}