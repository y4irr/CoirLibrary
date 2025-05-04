package dev.y4irr.coir.menu

import dev.y4irr.coir.menu.impl.MoveItemToPlayer
import org.bukkit.entity.Player
import java.util.*

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

object MenuManager {

    private val menus = mutableMapOf<UUID, MenuSession>()
    private val activeClickLocks = mutableSetOf<UUID>()

    fun open(
        player: Player,
        menu: Menu,
        updateAfterClick: Boolean,
        cancelInventory: Boolean,
        usePlayerInventory: Boolean = false
    ) {
        val moveItemToPlayer = menu::class.annotations.filterIsInstance<MoveItemToPlayer>().firstOrNull()?.value ?: false
        menus[player.uniqueId] = MenuSession(menu, updateAfterClick, cancelInventory, usePlayerInventory, moveItemToPlayer)
    }

    fun isClickLocked(player: Player): Boolean {
        return activeClickLocks.contains(player.uniqueId)
    }

    fun lockClick(player: Player) {
        activeClickLocks.add(player.uniqueId)
    }

    fun unlockClick(player: Player) {
        activeClickLocks.remove(player.uniqueId)
    }

    fun get(player: Player): Menu? = menus[player.uniqueId]?.menu
    fun getSession(player: Player): MenuSession? = menus[player.uniqueId]
    fun remove(player: Player) {
        menus.remove(player.uniqueId)
    }

    data class MenuSession(
        val menu: Menu,
        val updateAfterClick: Boolean,
        val cancelInventory: Boolean,
        val usePlayerInventory: Boolean,
        val moveItemToPlayer: Boolean
    )
}