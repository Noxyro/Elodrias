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

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class ElodriasCommand : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, alias: String, arguments: Array<out String>): Boolean {
        // TODO("Not yet implemented")
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, arguments: Array<out String>): MutableList<String> {
        // TODO("Not yet implemented")
        return mutableListOf()
    }
}