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

class SetHomeCommand(private val homes: Homes) : CommandModule(homes.plugin, "set", "Sets your home!", "/sethome") {

	override fun onCommand(sender: CommandSender, alias: String, arguments: Array<out String>) {
		if (sender is Player) {
			onPlayerCommand(sender, arguments)
			return
		}

		setHomeOtherCommand(sender, arguments)
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
			setHomeCommand(player)
			return
		}

		if (!player.hasPermission("${command.permission}.other")) {
			player.sendMessage("You don't have permission for this!")
			return
		}

		when (arguments.size) {
			1 -> setHomeOtherCommand(player, arguments[0], player.location)
			else -> setHomeOtherCommand(player, arguments)
		}
	}

	private fun setHomeCommand(player: Player) {
		homes.setHome(player.uniqueId, player.location)
		player.sendMessage("Home set!")
	}

	private fun setHomeOtherCommand(sender: CommandSender, arguments: Array<out String>) {
		when (arguments.size) {
			0 -> sender.sendMessage("Please enter player!")
			1 -> sender.sendMessage("Please enter world!")
			2 -> sender.sendMessage("Please enter X!")
			3 -> sender.sendMessage("Please enter Y!")
			4 -> sender.sendMessage("Please enter Z!")
			5 -> setHomeOtherCommand(sender, arguments[0], arguments[1], arguments[2], arguments[3], arguments[4])
			6 -> setHomeOtherCommand(
				sender,
				arguments[0],
				arguments[1],
				arguments[2],
				arguments[3],
				arguments[4],
				arguments[5]
			)
			7 -> setHomeOtherCommand(
				sender,
				arguments[0],
				arguments[1],
				arguments[2],
				arguments[3],
				arguments[4],
				arguments[5],
				arguments[6]
			)
		}
	}

	private fun setHomeOtherCommand(sender: CommandSender, name: String, location: Location) {
		val target = homes.findPlayer(sender, name) ?: return
		homes.setHome(target.uniqueId, location)
		sender.sendMessage("Home of ${target.name} set!")
	}

	private fun setHomeOtherCommand(
		sender: CommandSender,
		target: String,
		world: String,
		x: Double,
		y: Double,
		z: Double,
		yaw: Float = 0.0f,
		pitch: Float = 0.0f
	) {
		homes.plugin.server.getWorld(world).let {
			if (it == null) {
				sender.sendMessage("No world with this name!"); return
			}
			setHomeOtherCommand(sender, target, Location(it, x, y, z, yaw, pitch))
		}
	}

	private fun setHomeOtherCommand(
		sender: CommandSender,
		target: String,
		world: String,
		x: String,
		y: String,
		z: String,
		yaw: String = "0.0",
		pitch: String = "0.0"
	) {
		try {
			setHomeOtherCommand(
				sender,
				target,
				world,
				x.toDouble(),
				y.toDouble(),
				z.toDouble(),
				yaw.toFloat(),
				pitch.toFloat()
			)
		} catch (ex: NumberFormatException) {
			sender.sendMessage("Please enter valid numbers!")
		}
	}
}