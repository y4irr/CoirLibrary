package dev.y4irr.coir.menu

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.plugin.Plugin
import java.util.*

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ Hub
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

class MenuListener(private val plugin: Plugin) : Listener {

    private val clickCooldown = mutableMapOf<UUID, Long>()

    @EventHandler(priority = EventPriority.HIGH)
    fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val session = MenuManager.getSession(player) ?: return
        val currentMenu = session.menu

        val top = player.openInventory.topInventory
        val rawSlot = event.rawSlot
        val slot = event.slot

        val isTop = rawSlot == slot
        val isBottom = rawSlot >= top.size

        if (session.usePlayerInventory) {
            if (isBottom) {
                val button = currentMenu.playerButtons[slot] ?: return
                button.clicked(player, slot, event.click, event.hotbarButton)
                event.isCancelled = true
                return
            }

            if (!session.cancelInventory && isBottom.not()) {
                val moveToPlayer = session.moveItemToPlayer
                if (!moveToPlayer) {
                    if (event.click == ClickType.DROP || event.click.name.contains("OUTSIDE")) {
                        Bukkit.getScheduler().runTaskLater(plugin, {
                            player.openInventory.topInventory.setItem(slot, event.currentItem)
                        }, 1L)
                        event.isCancelled = true
                    }
                }
                return
            }
        }

        event.isCancelled = session.cancelInventory

        if (!isTop || slot < 0 || slot >= top.size) return
        val button = currentMenu.buttons[slot] ?: return

        if (button.shouldCancel(player, slot, event.click) && button.shouldShift(player, slot, event.click)) {
            event.isCancelled = true
        }

        try {
            button.clicked(player, slot, event.click, event.hotbarButton)
        } finally {
            val afterClickMenu = MenuManager.get(player)
            if (afterClickMenu != null && afterClickMenu !== currentMenu) {
                Bukkit.getScheduler().runTask(plugin) {
                    afterClickMenu.open(player)
                }
                return
            }

            if (session.updateAfterClick || button.shouldUpdate(player, slot, event.click)) {
                Bukkit.getScheduler().runTask(plugin) {
                    currentMenu.open(player)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        val session = MenuManager.getSession(player) ?: return

        session.menu.onClose(player, event)
        MenuManager.remove(player)
    }
}