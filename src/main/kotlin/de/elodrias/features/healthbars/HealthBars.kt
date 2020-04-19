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

import de.elodrias.features.healthbars.listener.HealthBarsListener
import de.elodrias.module.Module
import net.md_5.bungee.api.ChatColor
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import kotlin.math.ceil

class HealthBars(plugin: Plugin) : Module(plugin, HealthBars::class.java) {

    private val colors = listOf(
            ChatColor.DARK_RED,
            ChatColor.RED,
            ChatColor.GOLD,
            ChatColor.YELLOW,
            ChatColor.GREEN,
            ChatColor.DARK_GREEN
    )

    private val healthBarCache: MutableMap<Int, HealthBar> = mutableMapOf()

    override fun init() {
        registerListener(HealthBarsListener(this))
        registerInitializers(Entity::class.java, {
            if (it is LivingEntity) {
                if (it !is Player) {
                    it.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value?.let { maxHealth ->
                        it.customName = getHealthBar(it.health, maxHealth)
                        it.isCustomNameVisible = it.health != maxHealth
                    }
                }
            }
        })
    }

    /*private fun generateHealthBars(length: Int): Map<Int, String> {
        if (length <= 0) throw IllegalArgumentException("Length of health bars can not be zero or negative")
        if (healthBarStrings.containsKey(length)) return healthBarStrings[length]!!

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
    }*/

    fun getHealthBar(health: Double, maxHealth: Double, length: Int = maxHealth.toInt()): String {
        val relative = (if (health > maxHealth) 1.0 else if (health < 0 || maxHealth == 0.0) 0.0 else health / maxHealth)
        return healthBarCache.getOrPut(length) {
            HealthBar(length)
        }.getFilledTo(ceil(relative * length).toInt(), pickColor(relative))
    }

    fun getHealthBar(livingEntity: LivingEntity): String {
        return getHealthBar(livingEntity.health, livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value
                ?: throw Exception("Tried getting max health attribute from entity without max health"))
    }

    private fun pickColor(relative: Double): ChatColor {
        if (relative <= 0.0) return colors[0]
        return colors[ceil(relative * colors.size - 1).toInt()]
    }

}