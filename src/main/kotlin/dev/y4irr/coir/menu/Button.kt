package dev.y4irr.coir.menu

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ Hub
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

abstract class Button {
    abstract fun getItem(player: Player): ItemStack?

    open fun clicked(player: Player, slot: Int, click: ClickType, hotbar: Int) {}
    open fun shouldUpdate(player: Player, slot: Int, click: ClickType): Boolean = false
    open fun shouldCancel(player: Player, slot: Int, click: ClickType): Boolean = true
    open fun shouldShift(player: Player, slot: Int, click: ClickType): Boolean = true
}