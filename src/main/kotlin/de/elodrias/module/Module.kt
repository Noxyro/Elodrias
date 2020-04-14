/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.module

import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

abstract class Module(
        val plugin: Plugin,
        val name: String
) {

    val listeners = mutableListOf<Listener>()

    protected fun registerListener(listener: Listener) {
        listeners.add(listener)
    }

    protected fun unregisterListener(listener: Listener) {
        listeners.remove(listener)
    }

    fun createNamespacedKey(key: String): NamespacedKey {
        return NamespacedKey(plugin, "$name.$key")
    }

}