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
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.*

data class TeleportTask(
	val plugin: Plugin,
	val entity: UUID,
	val target: TeleportTarget,
	val interruptible: Boolean = true,
	val delay: Long = 200,
	val scheduled: Long = System.currentTimeMillis()
) {

	val task: BukkitTask = plugin.server.scheduler.runTaskLater(plugin, Runnable {
		if (!target.isValid()) return@Runnable

		plugin.server.getEntity(entity)?.teleport(target.getLocation()!!)
		this.cancel()
	}, delay)

	constructor(
		plugin: Plugin,
		entity: Entity,
		target: TeleportTarget,
		interruptible: Boolean = true,
		delay: Long = 200
	) : this(plugin, entity.uniqueId, target, interruptible, delay)

	constructor(
		plugin: Plugin,
		entity: Entity,
		target: Entity,
		interruptible: Boolean = true,
		delay: Long = 200
	) : this(plugin, entity, EntityTeleportTarget(plugin.server, target.uniqueId), interruptible, delay)

	constructor(
		plugin: Plugin,
		entity: Entity,
		location: Location,
		interruptible: Boolean = true,
		delay: Long = 200
	) : this(plugin, entity, LocationTeleportTarget(location), interruptible, delay)

	constructor(
		plugin: Plugin,
		entity: Entity,
		x: Double,
		y: Double,
		z: Double,
		world: World = entity.world,
		interruptible: Boolean = true,
		delay: Long = 200
	) : this(plugin, entity, LocationTeleportTarget(world, x, y, z), interruptible, delay)

	fun getTicksRemaining(): Double = delay - ((System.currentTimeMillis() - scheduled) / 20000.0)

	fun isCanceled() = task.isCancelled

	fun cancel() = task.cancel()

}