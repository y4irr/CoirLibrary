package dev.y4irr.coir.scoreboard

import dev.y4irr.coir.scoreboard.annotations.DebugMode
import dev.y4irr.coir.scoreboard.annotations.SbOptions
import dev.y4irr.coir.scoreboard.annotations.Tickable
import dev.y4irr.coir.scoreboard.board.Board
import dev.y4irr.coir.scoreboard.board.BoardUpdateThread
import dev.y4irr.coir.scoreboard.events.BoardCreateEvent
import dev.y4irr.coir.scoreboard.listener.BoardEventLogger
import dev.y4irr.coir.scoreboard.listener.BoardListener
import dev.y4irr.coir.scoreboard.utils.getAnnotation
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.concurrent.ConcurrentHashMap

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

class Wai(
    val plugin: Plugin,
    private val provider: WaiAdapter
) {

    private val boards = ConcurrentHashMap<Player, Board>()
    val lastUpdateMap = ConcurrentHashMap<Player, Long>()

    val debugMode: Boolean = provider.getAnnotation<DebugMode>()?.bool ?: false
    private val async: Boolean = provider.getAnnotation<SbOptions>()?.isAsync ?: false
    private val tick: Long = provider.getAnnotation<Tickable>()?.tick ?: 20L

    private val updateThread = BoardUpdateThread(this)

    fun start() {
        updateThread.start()
        Bukkit.getPluginManager().registerEvents(BoardListener(this), plugin)

        if (debugMode) {
            Bukkit.getPluginManager().registerEvents(BoardEventLogger(), plugin)
            plugin.logger.info("[Wai] Debug mode enabled via @DebugMode(true)")
        }
    }

    fun stop() {
        updateThread.stopRunning()
    }

    fun createBoard(player: Player) {
        if (boards.containsKey(player)) return

        val board = Board(player, this, provider, async, tick)
        boards[player] = board
        lastUpdateMap[player] = System.currentTimeMillis()

        if (debugMode) {
            plugin.logger.info("[Wai] Created board for ${player.name} (async=$async, tick=$tick)")
        }

        Bukkit.getPluginManager().callEvent(BoardCreateEvent(board, player))
    }

    fun removeBoard(player: Player) {
        if (debugMode) {
            plugin.logger.info("[Wai] Removing board for ${player.name}")
        }
        boards.remove(player)
        lastUpdateMap.remove(player)
    }

    fun clearBoards() {
        boards.clear()
        lastUpdateMap.clear()
    }

    fun getBoards(): Map<Player, Board> = boards
}