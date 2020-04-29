/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.features.homes.command

import de.elodrias.command.CommandModule
import de.elodrias.features.homes.Homes
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DeleteHomeCommand(private val homes: Homes) : CommandModule(homes.plugin, "delete", "/deletehome") {

	override fun onCommand(sender: CommandSender, alias: String, arguments: Array<out String>) {
		if (sender is Player) {
			onPlayerCommand(sender, arguments)
			return
		}

		deleteHomeOtherCommand(sender, arguments)
	}

	override fun onTabComplete(
		sender: CommandSender,
		alias: String,
		arguments: Array<out String>,
		location: Location?
	): MutableList<String> {
		return homes.plugin.onTabComplete(sender, this.command, alias, arguments) ?: mutableListOf()
	}

	private fun onPlayerCommand(player: Player, arguments: Array<out String>) {
		if (arguments.isEmpty()) {
			deleteHomeCommand(player)
			return
		}

		if (!player.hasPermission("${command.permission}.other")) {
			player.sendMessage("You don't have permission for this!")
			return
		}

		deleteHomeOtherCommand(player, arguments[0])
	}

	private fun deleteHomeCommand(player: Player) {
		homes.deleteHome(player.uniqueId)
		player.sendMessage("Home deleted!")
	}

	private fun deleteHomeOtherCommand(sender: CommandSender, arguments: Array<out String>) {
		when (arguments.size) {
			0 -> sender.sendMessage("Please enter player!")
			else -> deleteHomeOtherCommand(sender, arguments[0])
		}
	}

	private fun deleteHomeOtherCommand(sender: CommandSender, name: String) {
		val target = homes.findPlayer(sender, name) ?: return
		homes.deleteHome(target.uniqueId)
		sender.sendMessage("Home of ${target.name} deleted!")
	}

}