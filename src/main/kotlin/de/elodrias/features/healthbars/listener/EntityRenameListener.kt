/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.features.healthbars.listener

import de.elodrias.event.entity.EntityRenameEvent
import de.elodrias.features.healthbars.HealthBars
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class EntityRenameListener(
        private val healthBars: HealthBars
) : Listener {

    @EventHandler
    fun onEntityRename(event: EntityRenameEvent) {
        if (event.entity !is LivingEntity) return
        healthBars.storePersistentCustomName(event.entity, event.newName)
    }
}