/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.features.healthbars.event

import de.elodrias.event.CancellableEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.event.HandlerList

class HealthBarHideEvent(
        val livingEntity: LivingEntity,
        val healthBarString: String?,
        val customName: String?,
        val customNameVisible: Boolean
) : CancellableEvent() {

    companion object {
        @JvmStatic
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}