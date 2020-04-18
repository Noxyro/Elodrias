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

import de.elodrias.features.healthbars.HealthBars
import de.elodrias.features.healthbars.event.HealthBarUpdateEvent
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class HealthBarsListener(
        private val healthBars: HealthBars
) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityDamage(event: EntityDamageEvent) {
        // Already cancelled
        if (event.isCancelled) return
        // Not alive, no health bar
        if (event.entity !is LivingEntity) return
        // No damage, no change
        if (event.finalDamage == 0.0) return

        val livingEntity = event.entity as LivingEntity
        livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.let { maxHealth ->
            val healthBar = healthBars.getHealthBar(livingEntity.health - event.finalDamage, maxHealth.value)

            val updateEvent = HealthBarUpdateEvent(event.entity as LivingEntity, healthBar)
            healthBars.plugin.server.pluginManager.callEvent(updateEvent)
            if (updateEvent.isCancelled) return

            if (livingEntity.health - event.finalDamage == maxHealth.value) {
                livingEntity.isCustomNameVisible = false; return
            }

            /* TODO:
             *   Set health bar below / on top of name line
             *   https://www.spigotmc.org/threads/multiple-lines-of-player-name-tag.296928/
             *   https://bukkit.org/threads/tutorial-scoreboards-teams-with-the-bukkit-api.139655/
             */
            livingEntity.customName = healthBar
            livingEntity.isCustomNameVisible = true
        } ?: throw Exception("Tried getting max health attribute from entity without max health")
    }

}