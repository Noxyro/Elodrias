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

import de.elodrias.module.Feature
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.plugin.Plugin
import java.util.*

class Teleports(plugin: Plugin) : Feature(plugin, Teleports::class.java) {

    private val pendingTeleports: MutableMap<UUID, TeleportTask> = mutableMapOf()

    fun teleport(entity: Entity, target: Location, interruptible: Boolean = true, delay: Long = 200) {
        if (!checkEnabled(entity)) return

        // Remove old / cancelled task
        pendingTeleports[entity.uniqueId]?.let {
            if (it.isCanceled()) pendingTeleports.remove(entity.uniqueId)
        }

        if (pendingTeleports.containsKey(entity.uniqueId)) {
            entity.sendMessage("You already have a pending teleport!")
            return
        }

        pendingTeleports.putIfAbsent(entity.uniqueId, TeleportTask(plugin, entity, target, interruptible, delay))
    }

    fun teleport(entity: Entity, target: Entity, interruptible: Boolean = true, delay: Long = 200) {
        teleport(entity, target.location, interruptible, delay)
    }

    fun teleport(
        entity: Entity,
        world: World = entity.world,
        x: Double = entity.location.x,
        y: Double = entity.location.y,
        z: Double = entity.location.z,
        yaw: Float = 0.0f,
        pitch: Float = 0.0f,
        interruptible: Boolean = true,
        delay: Long = 200
    ) {
        teleport(entity, Location(world, x, y, z, yaw, pitch), interruptible, delay)
    }

    fun cancelTeleport(uuid: UUID) {
        if (!pendingTeleports.containsKey(uuid)) return

        pendingTeleports[uuid]!!.cancel()
        pendingTeleports.remove(uuid)
    }

    fun cancelTeleport(entity: Entity) {
        cancelTeleport(entity.uniqueId)
    }

}

