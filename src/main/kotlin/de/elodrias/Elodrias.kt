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

import de.elodrias.command.ElodriasCommand
import de.elodrias.economy.Economy
import de.elodrias.features.currencydrops.CurrencyDrops
import de.elodrias.features.healthbars.HealthBars
import de.elodrias.features.homes.Homes
import de.elodrias.features.teleports.Teleports
import de.elodrias.listener.ElodriasListener
import de.elodrias.module.Module
import de.elodrias.module.exception.ModuleAlreadyRegisteredException
import org.bukkit.plugin.java.JavaPlugin

class Elodrias : JavaPlugin() {

    private val modules: LinkedHashMap<String, Module> = linkedMapOf()

    override fun onLoad() {
        registerCoreModules()
        registerCoreCommands()

        modules.forEach { it.value.init() }
    }

    override fun onEnable() {
        this.server.pluginManager.registerEvents(ElodriasListener(), this)

        modules.forEach { it.value.enable() }
    }

    override fun onDisable() {
        modules.forEach { it.value.disable() }
    }

    private fun registerModule(module: Module) {
        if (modules.putIfAbsent(module.identifier, module) != null) {
            throw ModuleAlreadyRegisteredException(module.identifier)
        }
    }

    private fun registerCoreModules() {
        val economy = Economy(this)
        registerModule(economy)
        registerModule(CurrencyDrops(this, economy))
        registerModule(HealthBars(this))
        val teleports = Teleports(this)
        registerModule(teleports)
        registerModule(Homes(this, teleports))
    }

    private fun registerCoreCommands() {
        registerModule(ElodriasCommand(this))
    }

    fun getModules(): LinkedHashMap<String, Module> = modules
}