package dev.y4irr.coir.scoreboard.listener

import dev.y4irr.coir.scoreboard.Wai
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.PluginDisableEvent
import java.util.logging.Level

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

class BoardListener(private val wai: Wai) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        Bukkit.getScheduler().runTaskLater(wai.plugin, {
            if (player.isOnline) {
                try {
                    wai.createBoard(player)
                } catch (ex: Exception) {
                    wai.plugin.logger.log(Level.SEVERE, "Failed to create scoreboard for ${player.name}", ex)
                }
            }
        }, 10L)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        wai.getBoards()[event.player]?.destroy()
    }

    @EventHandler
    fun onPluginDisable(event: PluginDisableEvent) {
        if (event.plugin == wai.plugin) {
            wai.stop()
            wai.getBoards().values.forEach { it.destroy() }
            wai.clearBoards()
        }
    }
}