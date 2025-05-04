package dev.y4irr.coir.scoreboard.board

import org.bukkit.ChatColor
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

class BoardEntry(
    private val board: Board,
    private var text: String,
    private val identifier: String
) {

    private val team: Team

    init {
        val scoreboard: Scoreboard = board.scoreboard

        val teamName = identifier.take(16)
        val existing = scoreboard.getTeam(teamName)
        team = existing ?: scoreboard.registerNewTeam(teamName)

        if (!team.entries.contains(identifier)) {
            team.addEntry(identifier)
        }
    }

    fun send(score: Int) {
        val (prefix, suffix) = splitText(text)
        team.prefix = prefix
        team.suffix = suffix
        board.objective.getScore(identifier).score = score
    }

    fun remove() {
        board.scoreboard.resetScores(identifier)
        team.unregister()
    }

    private fun splitText(input: String): Pair<String, String> {
        val translated = ChatColor.translateAlternateColorCodes('&', input)
        if (translated.length <= 16) return translated to ""

        var prefix = translated.substring(0, 16)
        var suffix: String

        val lastColorIndex = prefix.lastIndexOf(ChatColor.COLOR_CHAR)
        if (lastColorIndex >= 14) {
            prefix = prefix.substring(0, lastColorIndex)
            suffix = ChatColor.getLastColors(translated.substring(0, 17)) + translated.substring(lastColorIndex + 2)
        } else {
            suffix = ChatColor.getLastColors(prefix) + translated.substring(16)
        }

        if (suffix.length > 16) suffix = suffix.substring(0, 16)
        return prefix to suffix
    }
}