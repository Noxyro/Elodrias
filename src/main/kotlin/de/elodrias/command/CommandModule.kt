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

/**
 * Represents a command which can be registered and unregistered from a [org.bukkit.Server].
 *
 * Holds an internal map of all registered sub [CommandModule]s with their respective aliases.
 *
 * Sub commands behave as if they were real commands, but are never registered to the [org.bukkit.Server],
 * as they are only handled internally by the main [CommandModule] itself.
 *
 * When the main command is executed with a (registered) sub command as the first argument, the registered
 * sub commands [onCommand] method will be called instead.
 *
 * To make a sub command available it need to be registered via [registerSubCommand].
 */
abstract class CommandModule(
		val plugin: Plugin,
		val name: String,
		val description: String = "$name command",
		val usage: String = "/$name",
		val aliases: MutableList<String> = mutableListOf(),
		val permission: String = "command.$name"
) : Module("command:$name") {

	/** The [CustomCommand] instance that was created with this command module. */
	val command: CustomCommand = CustomCommand(name, description, usage, aliases, permission)

	private val subCommands: MutableMap<String, CommandModule> = mutableMapOf()
	private val subCommandAliases: MutableMap<String, String> = mutableMapOf()

	/**
	 * Custom command wrapper for a default BukkitCommand class.
	 */
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

	/**
	 * Will be called when this command is being executed.
	 *
	 * If another sub command is registered and the first argument equals to the sub command, it will be called instead.
	 */
	abstract fun onCommand(sender: CommandSender, alias: String, arguments: Array<out String>)

	/**
	 * Will be called on any attempt to tab complete an argument of this command.
	 */
	abstract fun onTabComplete(
			sender: CommandSender,
			alias: String,
			arguments: Array<out String>,
			location: Location?
	): MutableList<String>

	/**
	 * Registers a new sub command to this command.
	 *
	 * Sub commands are not registered as fully qualified commands on the server.
	 *
	 * They are always handled by their respective main command and are indirectly called through it.
	 */
	protected fun registerSubCommand(command: CommandModule) {
		subCommands[command.name] = command
		registerSubCommandAliases(command)
	}


	/**
	 * Registers all names and aliases of the given sub command as aliases for the main command.
	 */
	private fun registerSubCommandAliases(command: CommandModule) {
		subCommandAliases["${command.name}$name"] = command.name
		command.aliases.forEach { alias ->
			subCommandAliases[alias] = command.name
		}
	}

	/**
	 * Executes the sub command with the given name, if it is registered.
	 *
	 * Calling this method will cut away the first entry of [arguments] by default.
	 * Use [trim] = 0 to override this behavior.
	 */
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

	/**
	 * Will be called when this command gets enabled.
	 *
	 * This command and all its sub commands / modules will always be initialized at this point.
	 *
	 * Will always be called before any sub commands / modules are enabled.
	 *
	 * Overriding this method without calling its super implementation, will prevent automatic registering of this
	 * command and all its sub commands. Therefore this method is final currently.
	 */
	final override fun onEnable() {
		register()
	}

	/**
	 * Will be called when this command gets disabled.
	 *
	 * Will always be called after all sub commands / modules are disabled.
	 *
	 * Overriding this method without calling its super implementation, will prevent automatic unregistering of this
	 * command and all its sub commands. Therefore this method is final currently.
	 */
	final override fun onDisable() {
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