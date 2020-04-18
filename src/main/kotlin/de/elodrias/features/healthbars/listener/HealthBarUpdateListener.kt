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
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class HealthBarUpdateListener(
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

        (event.entity as LivingEntity).let {
            it.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.let { maxHealth ->
                if (it.health - event.finalDamage == maxHealth.value) {
                    it.isCustomNameVisible = false; return
                }

                /* TODO:
                 *   Set health bar below / on top of name line
                 *   https://www.spigotmc.org/threads/multiple-lines-of-player-name-tag.296928/
                 *   https://bukkit.org/threads/tutorial-scoreboards-teams-with-the-bukkit-api.139655/
                 */
                it.customName = healthBars.getHealthBar(it.health - event.finalDamage, maxHealth.value)
                it.isCustomNameVisible = true
            } ?: throw Exception("Tried getting max health attribute from entity without max health")
        }
    }

}