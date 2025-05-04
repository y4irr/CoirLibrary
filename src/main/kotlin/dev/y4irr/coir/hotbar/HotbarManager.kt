package dev.y4irr.coir.hotbar

import org.bukkit.entity.Player

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

object HotbarManager {
    private val playerHotbars = mutableMapOf<String, List<HotbarItem>>()

    fun setHotbar(player: Player, items: List<HotbarItem>) {
        playerHotbars[player.name] = items
        for (item in items) {
            player.inventory.setItem(item.slot, item.item)
        }
    }

    fun getHotbarItem(player: Player, slot: Int): HotbarItem? {
        return playerHotbars[player.name]?.firstOrNull { it.slot == slot }
    }

    fun clear(player: Player) {
        playerHotbars.remove(player.name)
    }

    fun updateHotbarItemStack(player: Player, updated: HotbarItem) {
        player.inventory.setItem(updated.slot, updated.item)
        playerHotbars[player.name]?.let { list ->
            val updatedList = list.toMutableList()
            val index = updatedList.indexOfFirst { it.slot == updated.slot }
            if (index != -1) updatedList[index] = updated
            playerHotbars[player.name] = updatedList
        }
    }

    fun updateHotbarSlot(player: Player, item: HotbarItem) {
        val items = playerHotbars[player.name]?.toMutableList() ?: return
        val index = items.indexOfFirst { it.slot == item.slot }
        if (index != -1) {
            items[index] = item
            playerHotbars[player.name] = items
            player.inventory.setItem(item.slot, item.item)
        }
    }
}