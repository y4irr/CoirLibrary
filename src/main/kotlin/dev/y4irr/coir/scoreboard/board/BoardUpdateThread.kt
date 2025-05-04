package dev.y4irr.coir.scoreboard.board

import dev.y4irr.coir.scoreboard.Wai
import org.bukkit.Bukkit

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

class BoardUpdateThread(private val wai: Wai) : Thread("Wai-Thread") {

    private var running = true
    private var debugTickCounter = 0L

    override fun run() {
        while (running) {
            val now = System.currentTimeMillis()
            for ((player, board) in wai.getBoards()) {
                if (!player.isOnline) continue

                val interval = board.tick
                val last = wai.lastUpdateMap[player] ?: 0
                if (now - last >= interval) {
                    wai.lastUpdateMap[player] = now
                    if (board.isAsync) {
                        Bukkit.getScheduler().runTaskAsynchronously(wai.plugin) {
                            board.update()
                            if (wai.debugMode && debugTickCounter % 20 == 0L) {
                                wai.plugin.logger.info("[Wai] Async update: ${player.name}")
                            }
                        }
                    } else {
                        Bukkit.getScheduler().runTask(wai.plugin) {
                            board.update()
                            if (wai.debugMode && debugTickCounter % 20 == 0L) {
                                wai.plugin.logger.info("[Wai] Sync update: ${player.name}")
                            }
                        }
                    }
                }
            }

            if (wai.debugMode && debugTickCounter % 20 == 0L) {
                wai.plugin.logger.info("[Wai] Update thread running... (${wai.getBoards().size} boards tracked)")
            }

            debugTickCounter++

            try {
                sleep(50L)
            } catch (e: InterruptedException) {
                break
            }
        }
    }

    fun stopRunning() {
        running = false
    }
}