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

import de.elodrias.features.healthbars.listener.HealthBarUpdateListener
import de.elodrias.module.Module
import net.md_5.bungee.api.ChatColor
import org.bukkit.plugin.Plugin
import kotlin.math.ceil

class HealthBars(plugin: Plugin) : Module(plugin, HealthBars::class.java) {

    private val healthBarChar = '|'
    private val healthBarPrefix = "${ChatColor.WHITE}("
    private val healthBarSuffix = "${ChatColor.WHITE})"
    private val colors = listOf(
            ChatColor.BLACK,
            ChatColor.DARK_RED,
            ChatColor.RED,
            ChatColor.GOLD,
            ChatColor.YELLOW,
            ChatColor.GREEN,
            ChatColor.DARK_GREEN
    )

    private val healthBarCache: MutableMap<Int, Map<Int, String>> = mutableMapOf()

    override fun init() {
        registerListener(HealthBarUpdateListener(this))
    }

    private fun generateHealthBars(length: Int): Map<Int, String> {
        if (length <= 0) throw IllegalArgumentException("Length of health bars can not be zero or negative")
        if (healthBarCache.containsKey(length)) return healthBarCache[length]!!

        val sb = StringBuilder()
        val generatedHealthBars: MutableMap<Int, String> = mutableMapOf()
        for (num in 0 until length) {
            sb.clear()
            sb.append(healthBarPrefix)
            sb.append(colors[ceil((num / length.toDouble()) * (colors.size - 1)).toInt()])
            sb.append((0 until num).map { healthBarChar }.joinToString(""))
            sb.append(colors[0])
            sb.append((num until length).map { healthBarChar }.joinToString(""))
            sb.append(healthBarSuffix)
            generatedHealthBars[num] = sb.toString()
        }

        return generatedHealthBars
    }

    fun getHealthBar(health: Double, maxHealth: Double, length: Int = maxHealth.toInt()): String {
        val relativeLength = ceil((if (health > maxHealth) 1.0 else if (health < 0 || maxHealth == 0.0) 0.0 else health / maxHealth) * length).toInt()
        return healthBarCache.getOrPut(length) {
            generateHealthBars(length)
        }.getOrElse(relativeLength) {
            throw Exception("No health bar of length $length found for value $relativeLength")
        }
    }

}