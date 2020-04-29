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
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.help.GenericCommandHelpTopic
import org.bukkit.plugin.Plugin
import java.lang.Integer.min
import java.lang.reflect.InvocationTargetException

abstract class CommandModule(
	val plugin: Plugin,
	val name: String,
	val description: String = "$name command",
	val usage: String = "/$name",
	val aliases: MutableList<String> = mutableListOf(),
	val permission: String = "command.$name"
) : Module("command:$name") {

	val command: CustomCommand = CustomCommand(name, description, usage, aliases, permission)

	private val subCommands: MutableMap<String, CommandModule> = mutableMapOf()
	private val subCommandAliases: MutableMap<String, String> = mutableMapOf()

	inner class CustomCommand(
		name: String,
		description: String,
		usage: String,
		aliases: MutableList<String>,
		permission: String
	) : BukkitCommand(name, description, usage, aliases) {

		init {
			setPermission(permission)
		}

		override fun execute(sender: CommandSender, alias: String, arguments: Array<out String>): Boolean {
			if (subCommandAliases.containsKey(alias)) {
				executeSubCommand(subCommandAliases[alias]!!, sender, arguments, 0)
				return true
			}

			if (arguments.isNotEmpty() && subCommands.containsKey(arguments[0])) {
				executeSubCommand(arguments[0], sender, arguments)
				return true
			}

			onCommand(sender, alias, arguments)
			return true
		}

		override fun tabComplete(
			sender: CommandSender,
			alias: String,
			arguments: Array<out String>,
			location: Location?
		): MutableList<String> {
			if (subCommandAliases.containsKey(alias)) {
				return subCommands[subCommandAliases[alias]]?.onTabComplete(sender, alias, arguments, location)
					?: mutableListOf()
			}

			return onTabComplete(sender, alias, arguments, location)
		}
	}

	abstract fun onCommand(sender: CommandSender, alias: String, arguments: Array<out String>)

	abstract fun onTabComplete(
		sender: CommandSender,
		alias: String,
		arguments: Array<out String>,
		location: Location?
	): MutableList<String>

	protected fun registerSubCommand(command: CommandModule) {
		subCommands[command.name] = command
		registerSubCommandAliases(command)
	}

	private fun registerSubCommandAliases(command: CommandModule) {
		subCommandAliases["${command.name}$name"] = command.name
		command.aliases.forEach { alias ->
			subCommandAliases[alias] = command.name
		}
	}

	protected fun executeSubCommand(name: String, sender: CommandSender, arguments: Array<out String>, trim: Int = 1) {
		if (!sender.hasPermission("$permission.$name")) {
			sender.sendMessage("No permissions for this!")
			return
		}

		subCommands[name]?.onCommand(
			sender,
			name,
			if (trim > 0) arguments.copyOfRange(min(trim, arguments.size), arguments.size) else arguments
		)
	}

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
		if (!commandMap.register(plugin.name, command)) throw CommandAlreadyRegisteredException(name)

		val commands =
			getFieldValue(commandMap.javaClass.superclass, "knownCommands", commandMap) as HashMap<String, Command>

		// Add sub command aliases to knownCommands
		subCommandAliases.forEach { entry ->
			commands[entry.key] = command
			commands["${plugin.name.toLowerCase()}:${entry.key}"] = command

			// Generate helpMap entries for sub commands
			subCommands[entry.value]?.let {
				server.helpMap.addTopic(
					GenericCommandHelpTopic(
						CustomCommand(
							entry.key,
							it.description,
							it.usage,
							it.aliases,
							"${it.permission}.${entry.key}"
						).apply { this.register(commandMap) }
					)
				)
			}
		}

		return this
	}

	private fun unregister() {
		val server = plugin.server
		val commandMap = getFieldValue(server.javaClass, "commandMap", server) as SimpleCommandMap
		val commands = getFieldValue(commandMap.javaClass.superclass, "knownCommands", commandMap) as HashMap<*, *>

		// Decouple command from commandMap
		commandMap.getCommand(name)?.unregister(commandMap)

		// Remove command and aliases from knownCommands
		commands.remove(name)
		commands.remove("${plugin.name.toLowerCase()}:$name")
		aliases.forEach { alias ->
			commands.remove(alias)
			commands.remove("${plugin.name.toLowerCase()}:$alias")
		}

		// Remove sub command aliases from knownCommands
		subCommandAliases.forEach { alias ->
			commands.remove(alias)
			commands.remove("${plugin.name.toLowerCase()}:$alias")
		}

		// Delete helpMap entries
		server.helpMap.helpTopics.filter { topic ->
			topic.name.equals("/$name", true) || aliases.any { topic.name.equals("/$it", true) }
		}.forEach {
			server.helpMap.helpTopics.remove(it)
		}

		// Sync commands to players
		invokePrivateMethod(server.javaClass, "syncCommands", server)

		// Clear sub command aliases
		subCommandAliases.clear()
	}

	/* private fun registerSubCommandAliases() {
		subModules.forEach { entry ->
			if (entry.value is CommandModule) {
				(entry.value as CommandModule).let { module ->
					subCommandAliases[module.name] = module
					module.registerSubCommandAliases()
					module.aliases.forEach { alias ->
						subCommandAliases[alias] = module
					}
				}
			}
		}
	} */

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

	private fun invokePrivateMethod(clazz: Class<*>, method: String, obj: Any) {
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