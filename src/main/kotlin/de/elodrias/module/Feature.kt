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
import org.bukkit.command.CommandSender
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

    /**
     * Registers a new listener to this feature.
     *
     * Registering listeners more than once can lead to unwanted or unexpected behavior.
     */
    protected fun registerListener(listener: Listener) {
        listeners.add(listener)
    }

    /**
     * Unregisters a listener from this feature, if it was registered.
     */
    protected fun unregisterListener(listener: Listener) {
        listeners.remove(listener)
    }

    /**
     * Registers a new initializer function to the given [clazz].
     *
     * If the provided [clazz] requested from the server, delivers any results, the given initializer will be called
     * in a for-each manor with every instance of said class.
     */
    protected fun registerInitializers(clazz: Class<*>, vararg initializers: (Any) -> Unit) {
        this.initializers.putIfAbsent(clazz, initializers)
    }

    /**
     * Unregisters all initializer function from this feature, that were registered on the given [clazz].
     */
    protected fun unregisterInitializers(clazz: Class<*>) {
        initializers.remove(clazz)
    }

    /**
     * Creates a [NamespacedKey] with the given key name belonging to this feature (and its plugin).
     *
     * The key will have the format of: <feature-identifier>.<key-name>
     */
    fun createNamespacedKey(key: String): NamespacedKey {
        return NamespacedKey(plugin, "${identifier}.$key")
    }

    override fun onInit() {}

    /**
     * Will be called at the end of this feature being disabled.
     */
    abstract fun onTeardown()

    /**
     * Will be called when this feature gets enabled.
     *
     * This feature and all its sub modules will always be initialized at this point.
     *
     * Will always be called before any sub modules are enabled.
     *
     * Overriding this method without calling the super implementation, will remove automatic listener registration and
     * invocation of any initializers. Therefore this method is final currently.
     */
    final override fun onEnable() {
        listeners.forEach { plugin.server.pluginManager.registerEvents(it, plugin) }
        initializers.forEach { invokeInitializer(it.key, it.value) }
    }

    /**
     * Will be called when this feature gets disabled.
     *
     * Will always be called after all sub modules are disabled.
     *
     * Overriding this method without calling the super implementation, will remove automatic listener unregistering,
     * cleanups and invocation of the [onTeardown] method. Therefore this method is final currently.
     */
    final override fun onDisable() {
        listeners.forEach { HandlerList.unregisterAll(it) }
        listeners.clear()
        initializers.clear()
        onTeardown()
    }

    /**
     * Invokes the given initializer [functions] with the given [clazz].
     */
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

    /**
     * Performs a simple [isEnabled] check with the given [sender] and outputs a message, if not enabled.
     */
    fun checkEnabled(sender: CommandSender): Boolean {
        if (!isEnabled()) {
            sender.sendMessage("This feature is not enabled currently!")
            return false
        }

        return true
    }

}