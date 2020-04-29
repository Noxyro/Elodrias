/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.util.extensions

import org.bukkit.OfflinePlayer
import org.bukkit.Server

fun Server.findPlayers(name: String): List<OfflinePlayer> {
	var target: OfflinePlayer? = this.getPlayerExact(name)
	if (target == null) target = this.getPlayer(name) else return listOf(target)
	if (target != null) return listOf(target)

	target = this.offlinePlayers.find { it.name.equals(name, true) }
	if (target != null) return listOf(target)

	this.onlinePlayers.filter { it.name.startsWith(name, true) }.let {
		if (it.isNotEmpty()) return it
	}

	this.onlinePlayers.filter { it.name.contains(name, true) }.let {
		if (it.isNotEmpty()) return it
	}

	return listOf()
}