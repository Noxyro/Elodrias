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

import de.elodrias.Elodrias
import de.elodrias.module.Module
import org.bukkit.Location
import org.bukkit.command.CommandSender
import kotlin.reflect.KFunction1

class ElodriasCommand(
    val elodrias: Elodrias
) : CommandModule(
    elodrias,
    "elodrias",
    "Elodrias main command",
    "/elodrias <subcommand> <parameters>",
    mutableListOf("elo")
) {

    override fun onCommand(sender: CommandSender, alias: String, arguments: Array<out String>): Boolean {
        when (arguments.size) {
            0 -> sender.sendMessage("${plugin.name} ${plugin.description.version}")
            else -> when (arguments[0]) {
                "module" -> onModuleCommand(sender, arguments)
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        alias: String,
        arguments: Array<out String>,
        location: Location?
    ): MutableList<String> {
        println("Tab complete?")
        return mutableListOf()
    }

    private fun onModuleCommand(sender: CommandSender, arguments: Array<out String>) {
        when (arguments.size) {
            1 -> sender.sendMessage("Specify action!")
            else -> when (arguments[1]) {
                "list" -> onModuleListCommand(sender)
                else -> when (arguments.size) {
                    2 -> sender.sendMessage("Specify module!")
                    else -> when (arguments[1]) {
                        "enable" -> onModuleEnableCommand(sender, arguments.copyOfRange(2, arguments.size))
                        "disable" -> onModuleDisableCommand(sender, arguments.copyOfRange(2, arguments.size))
                    }
                }
            }
        }
    }

    private fun onModuleListCommand(sender: CommandSender) {
        sender.sendMessage(elodrias.getModules().keys.joinToString())
    }

    private fun onModuleDisableCommand(
        sender: CommandSender,
        arguments: Array<out String>,
        parentModule: Module? = null
    ) {
        runModuleAction(sender, arguments, parentModule, Module::disable)
    }

    private fun onModuleEnableCommand(
        sender: CommandSender,
        arguments: Array<out String>,
        parentModule: Module? = null
    ) {
        runModuleAction(sender, arguments, parentModule, Module::enable)
    }

    private fun runModuleAction(
        sender: CommandSender,
        arguments: Array<out String>,
        parentModule: Module? = null,
        action: KFunction1<Module, Boolean>
    ) {
        (parentModule?.subModules ?: elodrias.getModules()).let {
            it[arguments[0]]?.let { module ->
                if (arguments.size == 1) {
                    if (!action.invoke(module)) sender.sendMessage("Module was already in state \"${action.name}\"!")
                } else {
                    runModuleAction(sender, arguments.copyOfRange(1, arguments.size), module, action)
                }
            } ?: sender.sendMessage("No module with this name found!")
        }
    }
}