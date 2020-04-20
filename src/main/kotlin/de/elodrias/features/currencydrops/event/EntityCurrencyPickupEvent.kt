/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.features.currencydrops.event

import de.elodrias.event.CancellableEvent
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.event.HandlerList

class EntityCurrencyPickupEvent(
        val entity: Entity,
        val item: Item,
        var amount: Double
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