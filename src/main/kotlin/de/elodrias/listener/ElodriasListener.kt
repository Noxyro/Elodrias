/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.listener

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ItemMergeEvent

class ElodriasListener : Listener {

    @EventHandler
    fun onItemMerge(event: ItemMergeEvent) {
        Bukkit.broadcastMessage("Merge event: ${event.entityType} | ${event.entity.itemStack} | ${event.target.itemStack}")
    }



}