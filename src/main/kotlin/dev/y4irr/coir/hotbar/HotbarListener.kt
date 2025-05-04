package dev.y4irr.coir.hotbar

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.plugin.Plugin

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

class HotbarListener(private val plugin: Plugin) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        HotbarSetup.applyTo(event.player)
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        Bukkit.getScheduler().runTaskLater(plugin, {
            HotbarSetup.applyTo(event.player)
        }, 2L)
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val hotbar = HotbarManager.getHotbarItem(event.entity, event.entity.inventory.heldItemSlot)
        event.drops.removeIf { drop -> hotbar != null && drop.isSimilar(hotbar.item) }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        HotbarManager.clear(event.player)
    }

    @EventHandler
    fun onClick(event: PlayerInteractEvent) {
        val player = event.player
        val slot = player.inventory.heldItemSlot
        val hotbarItem = HotbarManager.getHotbarItem(player, slot) ?: return
        val handItem = player.inventory.getItem(slot) ?: return

        if (!handItem.isSimilar(hotbarItem.item)) return

        val actionType = when (event.action) {
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> HotbarActionType.LEFT_CLICK
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> HotbarActionType.RIGHT_CLICK
            else -> null
        }

        if (hotbarItem.blockUse) {
            event.isCancelled = true
            player.updateInventory()
        }

        if (event.isBlockInHand && player.isSneaking) {
            hotbarItem.getAction(HotbarActionType.MIDDLE_CLICK)?.invoke(player)
            return
        }

        actionType?.let {
            hotbarItem.tryExecute(player, it)
        }
    }

}