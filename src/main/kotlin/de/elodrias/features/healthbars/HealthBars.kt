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

import de.elodrias.features.healthbars.event.HealthBarDisplayEvent
import de.elodrias.features.healthbars.event.HealthBarHideEvent
import de.elodrias.features.healthbars.event.HealthBarUpdateEvent
import de.elodrias.features.healthbars.listener.HealthBarsListener
import de.elodrias.module.Module
import net.md_5.bungee.api.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.*
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

    private val namespacedKeyCustom = NamespacedKey(plugin, "generic.name.custom")
    private val namespacedKeyCustomVisible = NamespacedKey(plugin, "generic.name.custom.visible")
    private val healthBarStringCache: MutableMap<Int, HealthBarString> = mutableMapOf()
    private val healthBarWorkers: MutableMap<UUID, BukkitTask> = mutableMapOf()

    override fun init() {
        registerListener(HealthBarsListener(this))
        /* registerInitializers(Entity::class.java, {
            if (it is LivingEntity) {
                if (it !is Player) {
                    it.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value?.let { maxHealth ->
                        it.customName = getHealthBarString(it.health, maxHealth)
                        it.isCustomNameVisible = it.health != maxHealth
                    }
                }
            }
        }) */
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

    /* TODO:
     *   Set health bar below / on top of name line
     *   https://www.spigotmc.org/threads/multiple-lines-of-player-name-tag.296928/
     *   https://bukkit.org/threads/tutorial-scoreboards-teams-with-the-bukkit-api.139655/
     */

    fun displayHealthBar(livingEntity: LivingEntity, health: Double = livingEntity.health, permanent: Boolean = false, ticks: Long = 100) {
        val displayEvent = HealthBarDisplayEvent(livingEntity, permanent, ticks)
        plugin.server.pluginManager.callEvent(displayEvent)

        if (displayEvent.isCancelled) return

        if (healthBarWorkers.containsKey(livingEntity.uniqueId)) healthBarWorkers[livingEntity.uniqueId]!!.cancel()
        if (!hasPersistentCustomName(livingEntity)) storePersistentCustomName(livingEntity)
        showHealthBar(livingEntity, health)

        if (permanent) return

        val uuid = livingEntity.uniqueId
        healthBarWorkers[uuid] = plugin.server.scheduler.runTaskLater(plugin, Runnable {
            hideHealthBar(uuid)
        }, ticks)
    }

    private fun showHealthBar(livingEntity: LivingEntity, health: Double) {
        livingEntity.isCustomNameVisible = true

        val updatedHealthBar = getHealthBarString(livingEntity, health)
        val healthBarUpdateEvent = HealthBarUpdateEvent(livingEntity, livingEntity.customName, updatedHealthBar)
        plugin.server.pluginManager.callEvent(healthBarUpdateEvent)

        if (healthBarUpdateEvent.isCancelled) return

        livingEntity.customName = updatedHealthBar
    }

    fun hideHealthBar(uuid: UUID) {
        val entity = plugin.server.getEntity(uuid) ?: return
        if (entity !is LivingEntity) return

        retrievePersistentCustomName(entity).let {
            val healthBarHideEvent = HealthBarHideEvent(entity, entity.customName, it.first, it.second)
            plugin.server.pluginManager.callEvent(healthBarHideEvent)
            if (healthBarHideEvent.isCancelled) return

            val previousName = if (it.first.equals("")) null else it.first
            entity.customName = previousName
            entity.isCustomNameVisible = it.second
        }

        healthBarWorkers.remove(uuid)
    }

    fun hasPersistentCustomName(livingEntity: LivingEntity): Boolean {
        return (livingEntity as PersistentDataHolder).persistentDataContainer.has(namespacedKeyCustom, PersistentDataType.STRING)
    }

    fun storePersistentCustomName(livingEntity: LivingEntity, customName: String? = livingEntity.customName) {
        (livingEntity as PersistentDataHolder).persistentDataContainer.let { container ->
            container.set(namespacedKeyCustom, PersistentDataType.STRING, customName ?: "")
            container.set(namespacedKeyCustomVisible, PersistentDataType.BYTE, if (livingEntity.isCustomNameVisible) 1.toByte() else 0.toByte())
        }
    }

    fun retrievePersistentCustomName(livingEntity: LivingEntity): Pair<String?, Boolean> {
        (livingEntity as PersistentDataHolder).persistentDataContainer.let { container ->
            return Pair(
                    container.get(namespacedKeyCustom, PersistentDataType.STRING),
                    container.get(namespacedKeyCustomVisible, PersistentDataType.BYTE)?.let { it.toInt() == 1 } ?: false
            )
        }
    }

    private fun getHealthBarString(health: Double, maxHealth: Double, length: Int = maxHealth.toInt()): String {
        val relative = (if (health > maxHealth) 1.0 else if (health < 0 || maxHealth == 0.0) 0.0 else health / maxHealth)
        return healthBarStringCache.getOrPut(length) {
            HealthBarString(length)
        }.getFilledTo(ceil(relative * length).toInt(), pickColor(relative).toString())
    }

    private fun getHealthBarString(livingEntity: LivingEntity, health: Double = livingEntity.health): String {
        return getHealthBarString(health, livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value
                ?: throw Exception("Tried getting max health attribute from entity without max health"))
    }

    private fun pickColor(relative: Double): ChatColor {
        if (relative <= 0.0) return colors[0]
        return colors[ceil(relative * colors.size - 1).toInt()]
    }

}