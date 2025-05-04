package dev.y4irr.coir.scoreboard.utils

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

inline fun <reified T : Annotation> Any.getAnnotation(): T? {
    return this.javaClass.getAnnotation(T::class.java)
}