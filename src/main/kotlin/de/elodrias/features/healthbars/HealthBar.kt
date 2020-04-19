/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.features.healthbars

import de.elodrias.util.extensions.repeat
import net.md_5.bungee.api.ChatColor

data class HealthBar(
        val length: Int,
        val symbol: Char = '|',
        val prefix: String = "${ChatColor.WHITE}(",
        val suffix: String = "${ChatColor.WHITE})"
) {

    private val stringCache: Array<Array<Array<String>>> = Array(ChatColor.values().size) { Array(ChatColor.values().size) { Array(length + 1) { "" } } }

    fun getFilledTo(amount: Int, barColor: ChatColor = ChatColor.WHITE, missingColor: ChatColor = ChatColor.BLACK): String {
        if (amount < 0) throw IllegalArgumentException("Can not get negatively filled health bar")
        if (amount > length) throw IllegalArgumentException("Can not get health bar larger than it's length")
        stringCache[missingColor.ordinal][barColor.ordinal][amount].let { healthBar ->
            if (healthBar == "") {
                return "$prefix$barColor${symbol.repeat(amount)}$missingColor${symbol.repeat(length - amount)}$suffix".also {
                    stringCache[missingColor.ordinal][barColor.ordinal][amount] = it
                }
            }

            return healthBar
        }
    }

}
