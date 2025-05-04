package dev.y4irr.coir.scoreboard.annotations

import dev.y4irr.coir.scoreboard.enums.BoardStyleType

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class BoardStyle(val type: BoardStyleType = BoardStyleType.ASCENDING)
