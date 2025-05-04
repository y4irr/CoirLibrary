package dev.y4irr.coir.scoreboard.events

import dev.y4irr.coir.scoreboard.board.Board
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

class BoardCreateEvent(val board: Board, val player: Player) : Event() {
    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic val handlerList = HandlerList()
    }
}