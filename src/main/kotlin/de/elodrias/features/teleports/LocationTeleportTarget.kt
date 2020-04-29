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
import org.bukkit.World

data class LocationTeleportTarget(
	val world: World?,
	val x: Double,
	val y: Double,
	val z: Double,
	val yaw: Float = 0.0f,
	val pitch: Float = 0.0f
) : TeleportTarget {

	constructor(location: Location) : this(
		location.world,
		location.x,
		location.y,
		location.z,
		location.yaw,
		location.pitch
	)

	private val location = Location(world, x, y, z, yaw, pitch)

	override fun getLocation(): Location? {
		return location
	}

	override fun isMoving(): Boolean {
		return false
	}

	override fun isValid(): Boolean {
		return location.world != null && location.isWorldLoaded
	}
}