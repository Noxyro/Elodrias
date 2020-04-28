/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.command

import de.elodrias.command.exception.CommandAlreadyRegisteredException
import de.elodrias.module.Module
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.plugin.Plugin
import java.lang.reflect.InvocationTargetException


abstract class CommandModule(
    val plugin: Plugin,
    val name: String,
    val description: String = "",
    val usage: String = "",
    val aliases: MutableList<String> = mutableListOf()
) : Module("command:$name") {

    inner class CustomCommand : BukkitCommand(name, description, usage, aliases) {
        override fun execute(sender: CommandSender, alias: String, arguments: Array<out String>): Boolean {
            return onCommand(sender, alias, arguments)
        }

        override fun tabComplete(
            sender: CommandSender,
            alias: String,
            args: Array<out String>,
            location: Location?
        ): MutableList<String> {
            return onTabComplete(sender, alias, args, location)
        }
    }

    abstract fun onCommand(sender: CommandSender, alias: String, arguments: Array<out String>): Boolean

    abstract fun onTabComplete(
        sender: CommandSender,
        alias: String,
        arguments: Array<out String>,
        location: Location?
    ): MutableList<String>

    override fun onInit() {}

    override fun onEnable() {
        register()
    }

    override fun onDisable() {
        unregister()
    }

    private fun register(): CommandModule {
        val server = plugin.server
        val commandMap = getFieldValue(server.javaClass, "commandMap", server) as SimpleCommandMap
        if (!commandMap.register(plugin.name, CustomCommand())) throw CommandAlreadyRegisteredException(name)

        return this
    }

    private fun unregister() {
        val server = plugin.server
        val commandMap = getFieldValue(server.javaClass, "commandMap", server) as SimpleCommandMap
        val commands = getFieldValue(commandMap.javaClass.superclass, "knownCommands", commandMap) as HashMap<*, *>

        // Remove command and aliases from knownCommands
        commands.remove(name)
        commands.remove("${plugin.name.toLowerCase()}:$name")
        aliases.forEach { alias ->
            commands.remove(alias)
            commands.remove("${plugin.name.toLowerCase()}:$alias")
        }

        // Decouple command from commandMap
        this.CustomCommand().unregister(commandMap)

        // Delete helpMap entries
        server.helpMap.helpTopics.filter { topic ->
            topic.name.equals("/$name", true) || aliases.any { topic.name.equals("/$it", true) }
        }.forEach {
            server.helpMap.helpTopics.remove(it)
        }

        // Sync commands to players
        invokePrivateMethod(server.javaClass, "syncCommands", server)
    }

    private fun getFieldValue(clazz: Class<*>, field: String, obj: Any): Any? {
        try {
            val objectField = clazz.getDeclaredField(field)
            objectField.isAccessible = true
            return objectField.get(obj)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        return null
    }

    private fun invokePrivateMethod(clazz: Class<*>, method: String, obj: Any, vararg args: Any?) {
        try {
            val objectMethod = clazz.getDeclaredMethod(method)
            objectMethod.isAccessible = true
            objectMethod.invoke(obj)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: java.lang.IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: ExceptionInInitializerError) {
            e.printStackTrace()
        }
    }
}