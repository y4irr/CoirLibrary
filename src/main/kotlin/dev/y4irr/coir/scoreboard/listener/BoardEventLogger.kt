package dev.y4irr.coir.scoreboard.listener

import dev.y4irr.coir.scoreboard.events.BoardChangeEvent
import dev.y4irr.coir.scoreboard.events.BoardCreateEvent
import dev.y4irr.coir.scoreboard.events.BoardDestroyEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

class BoardEventLogger : Listener {

    @EventHandler
    fun onCreate(event: BoardCreateEvent) {
        val board = event.board
        val player = event.player

        board.wai.plugin.logger.info(
            "[Wai] BoardCreateEvent → ${player.name} (tick=${board.tick}, async=${board.isAsync})"
        )
    }

    @EventHandler
    fun onChange(event: BoardChangeEvent) {
        val player = event.player
        val oldLines = event.oldLines
        val newLines = event.newLines

        event.board.wai.plugin.logger.info(
            "[Wai] BoardChangeEvent → ${player.name} changed lines\n" +
                    " - Old: $oldLines\n" +
                    " - New: $newLines"
        )
    }

    @EventHandler
    fun onDestroy(event: BoardDestroyEvent) {
        val player = event.player

        event.board.wai.plugin.logger.info("[Wai] BoardDestroyEvent → ${player.name}")
    }
}