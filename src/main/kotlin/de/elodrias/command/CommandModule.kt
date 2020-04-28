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
import java.lang.reflect.Field


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
        getPrivateField(server.javaClass, "commandMap")?.let { commandMapField ->
            (getFieldValue(commandMapField, server) as SimpleCommandMap).let { commandMap ->
                if (!commandMap.register(plugin.name, CustomCommand())) throw CommandAlreadyRegisteredException(name)
            }
        }

        return this
    }

    private fun unregister() {
        val server = plugin.server
        getPrivateField(server.javaClass, "commandMap")?.let { commandMapField ->
            (getFieldValue(commandMapField, server) as SimpleCommandMap).let { commandMap ->
                getPrivateField(commandMap.javaClass.superclass, "knownCommands")?.let { knownCommandsField ->
                    (getFieldValue(knownCommandsField, commandMap) as HashMap<*, *>).let { commands ->
                        commands.remove(name)
                        commands.remove("${plugin.name.toLowerCase()}:$name")
                        aliases.forEach { alias ->
                            commands.remove(alias)
                            commands.remove("${plugin.name.toLowerCase()}:$alias")
                        }

                        this.CustomCommand().unregister(commandMap)
                    }
                }
            }
        }

        server.helpMap.helpTopics.filter { topic ->
            topic.name.equals("/$name", true) || aliases.any { topic.name.equals("/$it", true) }
        }.forEach {
            server.helpMap.helpTopics.remove(it)
        }

        // TODO: CraftBukkit - CraftServer - syncCommands()?
        //  We need to update the commands for clients as well,
        //  otherwise they will still see command auto-completion.

    }

    private fun getFieldValue(field: Field, obj: Any): Any? {
        try {
            return field.get(obj)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        return null
    }

    private fun getPrivateField(clazz: Class<*>, field: String): Field? {
        try {
            val objectField = clazz.getDeclaredField(field)
            objectField.isAccessible = true
            return objectField
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        return null
    }
}