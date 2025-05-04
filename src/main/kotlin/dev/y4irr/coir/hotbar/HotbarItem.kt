package dev.y4irr.coir.hotbar

import org.bukkit.inventory.ItemStack
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

class HotbarItem(
    var item: ItemStack,
    val slot: Int,
    private val actions: MutableMap<HotbarActionType, (Player) -> Unit> = mutableMapOf()
) {
    var blockUse: Boolean = false
        private set

    private var cooldownSeconds: Int = 0
    private val cooldownMap: MutableMap<UUID, Long> = mutableMapOf()

    private val cooldownMessageMap: MutableMap<UUID, Long> = mutableMapOf()
    private val cooldownMessageSeconds: Long = 1

    fun blockUse(): HotbarItem {
        this.blockUse = true
        return this
    }

    fun putCooldown(seconds: Int): HotbarItem {
        this.cooldownSeconds = seconds
        return this
    }

    fun setAction(type: HotbarActionType, action: (Player) -> Unit): HotbarItem {
        actions[type] = action
        return this
    }

    fun getAction(type: HotbarActionType): ((Player) -> Unit)? {
        if (actions.isEmpty()) return null
        if (actions.size == 1) return actions.values.first()
        return actions[type]
    }

    fun tryExecute(player: Player, type: HotbarActionType) {
        if (isOnCooldown(player)) {
            if (canSendCooldownMessage(player)) {
                val remaining = getRemainingCooldown(player)
                player.sendMessage("§cYou're on cooldown for this item for $remaining seconds.")
                cooldownMessageMap[player.uniqueId] = System.currentTimeMillis()
            }
            return
        }

        getAction(type)?.invoke(player)
        markCooldown(player)
    }

    fun isOnCooldown(player: Player): Boolean {
        val lastUse = cooldownMap[player.uniqueId] ?: return false
        val elapsed = (System.currentTimeMillis() - lastUse) / 1000
        return elapsed < cooldownSeconds
    }

    fun getRemainingCooldown(player: Player): Long {
        val lastUse = cooldownMap[player.uniqueId] ?: return 0
        val elapsed = (System.currentTimeMillis() - lastUse) / 1000
        return (cooldownSeconds - elapsed).coerceAtLeast(1)
    }

    fun canSendCooldownMessage(player: Player): Boolean {
        val lastMsg = cooldownMessageMap[player.uniqueId] ?: return true
        val elapsed = (System.currentTimeMillis() - lastMsg) / 1000
        return elapsed >= cooldownMessageSeconds
    }

    fun getCooldownProgress(player: Player): Float {
        val lastUse = cooldownMap[player.uniqueId] ?: return 0f
        val elapsed = (System.currentTimeMillis() - lastUse) / 1000f
        return if (elapsed >= cooldownSeconds) 0f else (1f - (elapsed / cooldownSeconds)).coerceIn(0f, 1f)
    }

    fun markCooldown(player: Player) {
        if (cooldownSeconds > 0) {
            cooldownMap[player.uniqueId] = System.currentTimeMillis()
        }
    }
}