package dev.y4irr.coir.scoreboard.board

import dev.y4irr.coir.scoreboard.Wai
import dev.y4irr.coir.scoreboard.WaiAdapter
import dev.y4irr.coir.scoreboard.annotations.BoardDescending
import dev.y4irr.coir.scoreboard.annotations.BoardStartingNumber
import dev.y4irr.coir.scoreboard.annotations.BoardStyle
import dev.y4irr.coir.scoreboard.annotations.Title
import dev.y4irr.coir.scoreboard.enums.BoardStyleType
import dev.y4irr.coir.scoreboard.events.BoardChangeEvent
import dev.y4irr.coir.scoreboard.events.BoardDestroyEvent
import dev.y4irr.coir.scoreboard.utils.getAnnotation
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

class Board(
    val player: Player,
    val wai: Wai,
    private val provider: WaiAdapter,
    val isAsync: Boolean,
    val tick: Long
) {

    private var lastLines: List<String> = emptyList()

    val scoreboard: Scoreboard = Bukkit.getScoreboardManager().newScoreboard
    val objective: Objective = scoreboard.registerNewObjective("wai", "dummy")
    private val entries = mutableListOf<BoardEntry>()

    init {
        objective.displaySlot = DisplaySlot.SIDEBAR
        player.scoreboard = scoreboard
    }

    fun update() {
        val newLines = provider.getLines(player).take(15)

        if (newLines == lastLines) return

        if (wai.debugMode) {
            wai.plugin.logger.info("[Wai] Updating board for ${player.name}")
            wai.plugin.logger.info("[Wai] New lines: $newLines")
        }

        Bukkit.getPluginManager().callEvent(BoardChangeEvent(player, this, lastLines, newLines))
        lastLines = newLines

        objective.displayName = ChatColor.translateAlternateColorCodes('&', resolveTitle().take(32))

        scoreboard.entries.forEach { scoreboard.resetScores(it) }
        entries.forEach { it.remove() }
        entries.clear()

        newLines.forEachIndexed { index, line ->
            val score = resolvePosition(newLines.size - 1 - index) // solo el número se invierte
            val identifier = getSafeIdentifier(index)

            val entry = BoardEntry(this, line, identifier)
            entry.send(score)
            entries.add(entry)
        }
    }

    fun destroy() {
        if (wai.debugMode) {
            wai.plugin.logger.info("[Wai] Destroying board for ${player.name}")
        }
        Bukkit.getPluginManager().callEvent(BoardDestroyEvent(player, this))
        entries.forEach { it.remove() }
        entries.clear()
        player.scoreboard.clearSlot(DisplaySlot.SIDEBAR)
        wai.removeBoard(player)
    }

    private fun resolvePosition(index: Int): Int {
        val style = provider.getAnnotation<BoardStyle>()?.type ?: BoardStyleType.ASCENDING

        val descending = if (style == BoardStyleType.CUSTOM) {
            provider.getAnnotation<BoardDescending>()?.value ?: false
        } else {
            style.descending
        }

        val start = if (style == BoardStyleType.CUSTOM) {
            provider.getAnnotation<BoardStartingNumber>()?.value ?: 0
        } else {
            style.startNumber
        }

        return if (descending) start - index else start + index
    }

    private fun resolveTitle(): String {
        val titleAnnotation = provider.getAnnotation<Title>()
        val title = titleAnnotation?.title ?: "EMPTY"
        return if (title != "EMPTY") title else provider.getTitle(player)
    }

    private fun getSafeIdentifier(index: Int): String {
        val colors = ChatColor.values().filter { it != ChatColor.RESET }
        val color1 = colors[index % colors.size].toString()
        val color2 = colors[(index / colors.size) % colors.size].toString()
        return color1 + color2
    }
}