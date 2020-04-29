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

class HomeCommand(private val homes: Homes) : CommandModule(homes.plugin, "home", "Brings you home!") {

	override fun onInit() {
		registerSubCommand(SetHomeCommand(homes))
		registerSubCommand(DeleteHomeCommand(homes))
		registerSubCommand(OtherHomeCommand(homes))
	}

	override fun onCommand(sender: CommandSender, alias: String, arguments: Array<out String>) {
		when (arguments.size) {
			0 -> if (sender !is Player) sender.sendMessage("Only players can use this command!") else homeCommand(sender)
			else -> executeSubCommand("other", sender, arguments, 0)
		}
	}

	override fun onTabComplete(
		sender: CommandSender,
		alias: String,
		arguments: Array<out String>,
		location: Location?
	): MutableList<String> {
		return homes.plugin.onTabComplete(sender, this.command, alias, arguments) ?: mutableListOf()
	}

	private fun homeCommand(player: Player) {
		if (!homes.hasHome(player.uniqueId)) {
			player.sendMessage("Your home is not set yet!")
			return
		}

		homes.teleports.teleport(player, homes.getHome(player.uniqueId)!!, true)
		player.sendMessage("Teleporting in 10 seconds!")
	}
}