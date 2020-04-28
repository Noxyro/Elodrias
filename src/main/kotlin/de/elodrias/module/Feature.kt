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
import org.bukkit.entity.Entity
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

abstract class Feature(
    val plugin: Plugin,
    private val clazz: Class<*>,
    name: String = clazz.simpleName
) : Module(name) {

    private val listeners = mutableListOf<Listener>()
    private val initializers = mutableMapOf<Class<*>, Array<out (Any) -> Unit>>()

    protected fun registerListener(listener: Listener) {
        listeners.add(listener)
    }

    protected fun unregisterListener(listener: Listener) {
        listeners.remove(listener)
    }

    protected fun registerInitializers(clazz: Class<*>, vararg initializers: (Any) -> Unit) {
        this.initializers.putIfAbsent(clazz, initializers)
    }

    protected fun unregisterInitializers(clazz: Class<*>) {
        initializers.remove(clazz)
    }

    fun createNamespacedKey(key: String): NamespacedKey {
        return NamespacedKey(plugin, "${identifier}.$key")
    }

    override fun onInit() {}

    open fun onTeardown() {}

    override fun onEnable() {
        listeners.forEach { plugin.server.pluginManager.registerEvents(it, plugin) }
        initializers.forEach { invokeInitializer(it.key, it.value) }
    }

    override fun onDisable() {
        listeners.forEach { HandlerList.unregisterAll(it) }
        listeners.clear()
        initializers.clear()
        onTeardown()
    }

    private fun invokeInitializer(clazz: Class<*>, functions: Array<out (Any) -> Unit>) {
        val objects: Collection<Any>? = when (clazz) {
            Entity::class.java -> plugin.server.worlds.flatMap { world -> world.entities }
            else -> null
        }

        objects?.forEach { obj ->
            functions.forEach { init ->
                init.invoke(obj)
            }
        }
    }

}