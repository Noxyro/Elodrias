/*
 * Copyright (c) 2020 - Elodrias Project (https://elodrias.de)
 * by Noxyro (https://noxyro.me or https://github.com/noxyro)
 * This program comes with ABSOLUTELY NO WARRANTY.
 * This is free software, published under the GNU GPL v3 license
 * and you are welcome to redistribute it under certain conditions.
 * See full license (in project or at https://www.gnu.org/licenses/) for more details.
 *
 */

package de.elodrias.features.healthbars.util

import de.elodrias.util.extensions.repeat
import net.md_5.bungee.api.ChatColor

data class RepeatingCharPattern(
        val length: Int,
        val symbol: Char = '|',
        override val prefix: String = "${ChatColor.WHITE}(",
        override val suffix: String = "${ChatColor.WHITE})"
) : Prefixable, Suffixable {

    private val stringCache: MutableMap<String, MutableMap<String, MutableMap<Int, String>>> = mutableMapOf()

    fun repeatTo(indexLength: Int, preIndexPrefix: String, postIndexPrefix: String = ChatColor.BLACK.toString()): String {
        if (indexLength < 0) throw IllegalArgumentException("Index length can not be negative")
        if (indexLength > length) throw IllegalArgumentException("Index length can not be larger than length")
        return stringCache
                .getOrPut(postIndexPrefix) { mutableMapOf() }
                .getOrPut(preIndexPrefix) { mutableMapOf() }
                .getOrPut(indexLength) { "$prefix$preIndexPrefix${symbol.repeat(indexLength)}$postIndexPrefix${symbol.repeat(length - indexLength)}$suffix" }
    }

}
