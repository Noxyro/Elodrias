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
import java.util.*

class OtherHomeCommand(private val homes: Homes) :
	CommandModule(homes.plugin, "other", "Brings you to others home!", "/otherhome <player>") {

	override fun onCommand(sender: CommandSender, alias: String, arguments: Array<out String>) {
		if (sender is Player) {
			onPlayerCommand(sender, arguments)
			return
		}

		otherHomeCommand(sender, arguments)
	}

	override fun onTabComplete(
		sender: CommandSender,
		alias: String,
		arguments: Array<out String>,
		location: Location?
	): MutableList<String> {
		return mutableListOf()
	}

	private fun onPlayerCommand(player: Player, arguments: Array<out String>) {
		if (arguments.isEmpty()) {
			player.sendMessage("Please enter a player!")
			return
		}

		otherHomeCommand(player, arguments[0])
	}

	private fun otherHomeCommand(player: Player, target: UUID) {
		homes.getHome(target)?.let {
			homes.teleports.teleport(player, it, true)
			player.sendMessage("Teleporting in 10 seconds!")
		} ?: return player.sendMessage("This player does not have a home!")
	}

	private fun otherHomeCommand(player: Player, name: String) {
		val target = homes.findPlayer(player, name) ?: return
		otherHomeCommand(player, target.uniqueId)
	}

	private fun otherHomeCommand(sender: CommandSender, arguments: Array<out String>) {
		when (arguments.size) {
			0 -> sender.sendMessage("Please enter a player to teleport!")
			1 -> sender.sendMessage("Please enter a target to teleport to!")
			else -> homes.findPlayer(sender, arguments[0])?.let { player ->
				if (player.player == null) {
					sender.sendMessage("Player is not online!"); return
				}
				homes.findPlayer(sender, arguments[1])?.let { target ->
					homes.getHome(target.uniqueId)?.let {
						homes.teleports.teleport(player.player!!, it, true)
						sender.sendMessage("Teleporting ${player.name} in 10 seconds!")
					} ?: return sender.sendMessage("The target player does not have a home!")
				} ?: return
			} ?: return
		}
	}

}