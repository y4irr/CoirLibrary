package dev.y4irr.coir.scoreboard

import org.bukkit.entity.Player

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

interface WaiAdapter {
    fun getTitle(player: Player): String
    fun getLines(player: Player): List<String>
}