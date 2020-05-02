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

import de.elodrias.module.exception.ModuleAlreadyRegisteredException

abstract class Module(
        val identifier: String
) {

    private var initialized: Boolean = false

    private var enabled: Boolean = false

    val subModules = mutableMapOf<String, Module>()

    /**
     * Initializes this module.
     */
    fun init(): Boolean {
        if (initialized) return false

        onInit()
        subModules.forEach { it.value.init() }

        initialized = true
        return true
    }

    /**
     * Enables this module.
     */
    fun enable(): Boolean {
        if (enabled) return false
        if (!initialized) if (!init()) return false

        onEnable()
        subModules.forEach { it.value.enable() }

        enabled = true
        return true
    }

    /**
     * Disables this module.
     */
    fun disable(): Boolean {
        if (!enabled) return false

        subModules.forEach { it.value.disable() }
        onDisable()

        enabled = false
        initialized = false
        return true
    }

    /**
     * Returns if this module was already initialized or not.
     */
    fun isInitialized() = initialized

    /**
     * Returns if this module is currently enabled.
     */
    fun isEnabled() = enabled

    /**
     * Registers a new sub module to this module.
     *
     * Throws a [ModuleAlreadyRegisteredException], if another module with the same identifier (name)
     * is already registered.
     */
    protected fun registerSubModule(module: Module) {
        if (subModules.putIfAbsent(module.identifier, module) != null) throw ModuleAlreadyRegisteredException(module.identifier)
    }

    /**
     * Unregisters a sub module from this module, if present.
     */
    protected fun unregisterSubModule(module: Module) {
        subModules.remove(module.identifier)
    }

    /**
     * Will be called during initialization of this module.
     *
     * Will always be called before any sub modules are initialized.
     */
    protected abstract fun onInit()


    /**
     * Will be called when this module gets enabled.
     *
     * This module and all its sub modules will always be initialized at this point.
     *
     * Will always be called before any sub modules are enabled.
     */
    protected abstract fun onEnable()

    /**
     * Will be called when this module gets disabled.
     *
     * Will always be called after all sub modules are disabled.
     */
    protected abstract fun onDisable()

}