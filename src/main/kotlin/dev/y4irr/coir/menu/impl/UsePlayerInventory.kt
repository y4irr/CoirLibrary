package dev.y4irr.coir.menu.impl


/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ Hub
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class UsePlayerInventory(val value: Boolean = false)