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

abstract class Module(
    val identifier: String
) {

    private var initialized: Boolean = false

    private var enabled: Boolean = false

    val subModules = mutableMapOf<String, Module>()

    fun init(): Boolean {
        if (initialized) return false

        onInit()
        subModules.forEach { it.value.init() }

        initialized = true
        return true
    }

    fun enable(): Boolean {
        if (enabled) return false
        if (!initialized) if (!init()) return false

        onEnable()
        subModules.forEach { it.value.enable() }

        enabled = true
        return true
    }

    fun disable(): Boolean {
        if (!enabled) return false

        onDisable()
        subModules.forEach { it.value.disable() }

        enabled = false
        initialized = false
        return true
    }

    fun isInitialized() = initialized

    fun isEnabled() = enabled

    protected fun registerSubModule(module: Module) {
        subModules.putIfAbsent(module.identifier, module)
    }

    protected fun unregisterSubModule(module: Module) {
        subModules.remove(module.identifier)
    }

    protected abstract fun onInit()

    protected abstract fun onEnable()

    protected abstract fun onDisable()

}