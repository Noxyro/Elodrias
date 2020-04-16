/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias

import de.elodrias.economy.Economy
import de.elodrias.feature.moneydrop.MoneyDrop
import de.elodrias.listener.ElodriasListener
import de.elodrias.module.Module
import de.elodrias.module.exception.ModuleAlreadyRegisteredException
import de.elodrias.module.exception.ModuleNotRegisteredException
import org.bukkit.plugin.java.JavaPlugin

class Elodrias : JavaPlugin() {

    private val modules = mutableMapOf<String, Module>()

    override fun onEnable() {
        this.server.pluginManager.registerEvents(ElodriasListener(), this)
        registerModule(Economy(this))
        registerModule(MoneyDrop(this))
    }

    override fun onDisable() {
        val toRemove = modules.values
        toRemove.forEach { unregisterModule(it) }
    }

    private fun registerModule(module: Module) {
        modules.putIfAbsent(module.name, module)?.also { throw ModuleAlreadyRegisteredException(module.name) }
        module.listeners.forEach { this.server.pluginManager.registerEvents(it, this) }
    }

    private fun unregisterModule(module: Module) {
        modules.remove(module.name) ?: throw ModuleNotRegisteredException(module.name)
    }
}