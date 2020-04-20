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

data class HealthBarString(
        val length: Int,
        val symbol: Char = '|',
        val prefix: String = "${ChatColor.WHITE}(",
        val suffix: String = "${ChatColor.WHITE})"
) {

    private val stringCache: MutableMap<String, MutableMap<String, MutableMap<Int, String>>> = mutableMapOf()

    fun getFilledTo(amount: Int, filledPrefix: String, missingPrefix: String = ChatColor.BLACK.toString()): String {
        if (amount < 0) throw IllegalArgumentException("Amount can not be negative")
        if (amount > length) throw IllegalArgumentException("Amount can not be larger than length")
        return stringCache
                .getOrPut(missingPrefix) { mutableMapOf() }
                .getOrPut(filledPrefix) { mutableMapOf() }
                .getOrPut(amount) { "$prefix$filledPrefix${symbol.repeat(amount)}$missingPrefix${symbol.repeat(length - amount)}$suffix" }
    }

}
