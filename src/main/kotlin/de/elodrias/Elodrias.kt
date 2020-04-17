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
import de.elodrias.feature.currencydrops.CurrencyDrops
import de.elodrias.feature.healthbars.HealthBars
import de.elodrias.listener.ElodriasListener
import de.elodrias.module.Module
import de.elodrias.module.exception.ModuleAlreadyRegisteredException
import org.bukkit.plugin.java.JavaPlugin

class Elodrias : JavaPlugin() {

    private val modules: LinkedHashSet<Module> = linkedSetOf()

    override fun onLoad() {
        val economy = Economy(this)
        registerModule(economy)
        registerModule(CurrencyDrops(this, economy))
        registerModule(HealthBars(this))
        modules.forEach { it.init() }
    }

    override fun onEnable() {
        this.server.pluginManager.registerEvents(ElodriasListener(), this)
        registerModuleListeners()
        modules.forEach { it.enable() }
    }

    override fun onDisable() {
        modules.forEach { it.disable() }
    }

    private fun registerModule(module: Module) {
        if (!modules.add(module)) {
            throw ModuleAlreadyRegisteredException(module.getName())
        }
    }

    private fun registerModuleListeners() {
        modules.forEach { module -> module.listeners.forEach { this.server.pluginManager.registerEvents(it, this) } }
    }
}