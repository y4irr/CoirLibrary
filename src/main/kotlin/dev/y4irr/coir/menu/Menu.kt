package dev.y4irr.coir.menu

import dev.y4irr.coir.menu.impl.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ Hub
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

abstract class Menu {
    val buttons = mutableMapOf<Int, Button>()
    val playerButtons = mutableMapOf<Int, Button>()
    private val originalItems = mutableMapOf<Int, ItemStack>()
    private var inventory: Inventory? = null

    abstract fun getTitle(player: Player): String
    open fun getSize(): Int = 27
    open fun onOpen(player: Player) {}
    open fun onClose(player: Player, event: InventoryCloseEvent) {}
    open fun getPlaceholder(): ItemStack = ItemStack(Material.STAINED_GLASS_PANE, 1, 7)

    open fun getInventoryButtons(player: Player): Map<Int, Button> = emptyMap()

    fun open(player: Player) {
        val clazz = this::class
        val updateAfterClick = clazz.annotations.filterIsInstance<UpdateAfterClick>().firstOrNull()?.value ?: false
        val cancelInventory = clazz.annotations.filterIsInstance<CancelPlayerInventory>().firstOrNull()?.value ?: true
        val usePlayerInventory = clazz.annotations.filterIsInstance<UsePlayerInventory>().firstOrNull()?.value ?: false
        val title = getTitle(player).translated().take(32)

        val size = getSize()
        val inv = Bukkit.createInventory(player, size, title)
        inventory = inv

        buttons.clear()
        buttons.putAll(getButtons(player))

        for (i in 0 until size) {
            val button = buttons[i]
            inv.setItem(i, button?.getItem(player)?.translatedMeta() ?: getPlaceholder().translatedMeta())
        }

        if (usePlayerInventory) {
            originalItems.clear()
            playerButtons.clear()
            playerButtons.putAll(getInventoryButtons(player))

            for (slot in 9..35) {
                originalItems[slot] = player.inventory.getItem(slot) ?: ItemStack(Material.AIR)
                player.inventory.setItem(slot, null)
            }

            for ((slot, button) in playerButtons) {
                player.inventory.setItem(slot, button.getItem(player)?.translatedMeta())
            }
        }

        player.openInventory(inv)
        onOpen(player)

        MenuManager.open(player, this, updateAfterClick, cancelInventory, usePlayerInventory)
    }

    fun restoreInventory(player: Player) {
        if (originalItems.isNotEmpty()) {
            for ((slot, item) in originalItems) {
                player.inventory.setItem(slot, item)
            }
            originalItems.clear()
        }
    }

    abstract fun getButtons(player: Player): Map<Int, Button>
}

private fun String.translated(): String = replace('&', '§')

private fun ItemStack.translatedMeta(): ItemStack {
    val clone = this.clone()
    val meta = clone.itemMeta ?: return clone
    if (meta.hasDisplayName()) meta.displayName = meta.displayName.translated()
    if (meta.hasLore()) meta.lore = meta.lore.map { it.translated() }
    clone.itemMeta = meta
    return clone
}