package dev.y4irr.coir.hotbar

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable


/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

object HotbarSetup {

    private val registeredItems = mutableListOf<HotbarItem>()

    fun initialize(plugin: Plugin) {
        Bukkit.getPluginManager().registerEvents(HotbarListener(plugin), plugin)

        registeredItems.clear()
        start(plugin)
    }

    fun getRegisteredItems(): List<HotbarItem> = registeredItems

    fun registerItem(item: HotbarItem) {
        registeredItems.add(item)
    }

    private fun start(plugin: Plugin) {
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    val slot = player.inventory.heldItemSlot
                    val item = HotbarManager.getHotbarItem(player, slot)

                    if (item != null) {
                        val progress = item.getCooldownProgress(player)
                        player.exp = progress
                        player.level = if (progress > 0f) item.getRemainingCooldown(player).toInt() else 0
                    } else {
                        player.exp = 0f
                        player.level = 0
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 1L)
    }

    fun applyTo(player: Player) {
        HotbarManager.setHotbar(player, registeredItems)
    }
}