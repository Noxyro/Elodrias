/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.features.teleports

import org.bukkit.Location
import org.bukkit.Server
import java.util.*

data class EntityTeleportTarget(
	val server: Server,
	val entity: UUID
) : TeleportTarget {

	override fun getLocation(): Location? {
		return server.getEntity(entity)?.location
	}

	override fun isMoving(): Boolean {
		return true
	}

	override fun isValid(): Boolean {
		return server.getEntity(entity).let { it != null && it.isValid && !it.isDead }
	}
}