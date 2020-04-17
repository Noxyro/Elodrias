package de.elodrias.feature.healthbars

import de.elodrias.feature.healthbars.listener.HealthBarUpdateListener
import de.elodrias.module.Module
import net.md_5.bungee.api.ChatColor
import org.bukkit.plugin.Plugin
import kotlin.math.ceil

class HealthBars(plugin: Plugin) : Module(plugin, HealthBars::class.java) {

    private val healthBarChar = '|'
    private val healthBarPrefix = "${ChatColor.WHITE}["
    private val healthBarSuffix = "${ChatColor.WHITE}]"
    private val healthBarSymbols = 20
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

    private fun generateHealthBar(numSymbols: Int) {
        if (numSymbols < 0) return
        if (healthBarCache.containsKey(numSymbols)) return

        val sb = StringBuilder()
        val generatedHealthBars: MutableMap<Int, String> = mutableMapOf()
        for (num in 0 until healthBarSymbols) {
            sb.clear()
            sb.append(healthBarPrefix)
            sb.append(colors[ceil((num / healthBarSymbols.toDouble()) * (colors.size - 1)).toInt()])
            sb.append((0 until num).map { healthBarChar }.joinToString(""))
            sb.append(colors[0])
            sb.append((num until healthBarSymbols).map { healthBarChar }.joinToString(""))
            sb.append(healthBarSuffix)
            generatedHealthBars[num] = sb.toString()
        }

        healthBarCache[numSymbols] = generatedHealthBars

        /*healthBarCache.putIfAbsent(numSymbols,
            mapOf(*(0 until healthBarSymbols).map { num ->
                num to (healthBarPrefix +
                        colors[ceil((num / healthBarSymbols.toDouble()) * (colors.size - 1)).toInt()] +
                        (0 until num).map { healthBarChar }.joinToString() +
                        colors[0] +
                        (num until healthBarSymbols).map { healthBarChar }.joinToString() +
                        healthBarSuffix).also { plugin.logger.log(Level.INFO, "$num: $it") }
            }.toTypedArray())
        )*/
    }

    fun getHealthBar(health: Double, maxHealth: Double, numSymbols: Int = 20): String {
        return (ceil((0.0.coerceAtLeast(health) / maxHealth) * healthBarSymbols).toInt()).let {
            healthBarCache.getOrElse(numSymbols, {
                generateHealthBar(numSymbols)
                healthBarCache[numSymbols] ?: throw Exception("Couldn't retrieve $numSymbols long health bar")
            }).getOrElse(it) {
                throw Exception("Couldn't retrieve $it state from $numSymbols long health bar")
            }
        }
    }

}