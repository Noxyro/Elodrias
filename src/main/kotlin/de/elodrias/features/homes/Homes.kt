/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.features.homes

import de.elodrias.features.homes.command.HomeCommand
import de.elodrias.features.teleports.Teleports
import de.elodrias.module.Feature
import de.elodrias.util.extensions.findPlayers
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin
import java.util.*

class Homes(
	plugin: Plugin,
	val teleports: Teleports
) : Feature(plugin, Homes::class.java) {

	private val homesCache: MutableMap<UUID, Location> = mutableMapOf()

	override fun onInit() {
		registerSubModule(HomeCommand(this))
	}

	fun hasHome(uuid: UUID): Boolean {
		return homesCache.containsKey(uuid)
	}

	fun setHome(uuid: UUID, location: Location) {
		homesCache[uuid] = location
	}

	fun getHome(uuid: UUID): Location? {
		return homesCache[uuid]
	}

	fun deleteHome(uuid: UUID) {
		homesCache.remove(uuid)
	}

	fun setHome(entity: Entity) {
		setHome(entity.uniqueId, entity.location)
	}

	fun findPlayer(sender: CommandSender, name: String): OfflinePlayer? {
		val targets = plugin.server.findPlayers(name)
		if (targets.isEmpty()) {
			sender.sendMessage("No player with this name found!"); return null
		}
		if (targets.size > 1) {
			sender.sendMessage("Multiple players with this name found!\n${targets.joinToString()}"); return null
		}

		return targets[0]
	}

}