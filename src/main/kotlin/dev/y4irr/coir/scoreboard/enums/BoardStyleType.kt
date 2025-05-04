package dev.y4irr.coir.scoreboard.enums

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

enum class BoardStyleType(val descending: Boolean, val startNumber: Int) {
    CLASSIC(true, 15),
    REVERSE(true, -1),
    ASCENDING(false, 1),
    CUSTOM(false, 0);

    fun withDescending(desc: Boolean): BoardStyleType {
        return entries.first { it.name == this.name }.apply {
            descending == desc
        }
    }

    fun withStartNumber(start: Int): BoardStyleType {
        return entries.first { it.name == this.name }.apply {
            startNumber == start
        }
    }
}