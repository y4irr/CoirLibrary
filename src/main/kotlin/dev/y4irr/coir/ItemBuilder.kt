package dev.y4irr.coir

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.*
import java.lang.reflect.Field
import java.util.*
import java.util.concurrent.ThreadLocalRandom

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ CoirLibrary
 * @author Yair Soto @ 2025
 * Date: month:05 - day:02
 */

class ItemBuilder(val item: ItemStack) {

    constructor(material: Material, amount: Int = 1) : this(ItemStack(material, amount))
    constructor(material: Material, amount: Int, durability: Byte) : this(ItemStack(material, amount, durability.toShort()))
    constructor(material: Material, amount: Int, durability: Int) : this(ItemStack(material, amount, durability.toShort()))

    fun clone(): ItemBuilder = ItemBuilder(item.clone())

    fun getMeta(): ItemMeta = item.itemMeta

    fun setDurability(durability: Short): ItemBuilder {
        item.durability = durability
        return this
    }

    fun setDurability(durability: Int): ItemBuilder {
        item.durability = durability.toShort()
        return this
    }

    fun setAmount(amount: Int): ItemBuilder {
        item.amount = amount.coerceAtMost(64)
        return this
    }

    fun setName(name: String?): ItemBuilder {
        name?.let {
            item.itemMeta = item.itemMeta.apply { displayName = color(it) }
        }
        return this
    }

    fun setFireworkColor(color: Color): ItemBuilder {
        val meta = item.itemMeta
        if (meta is FireworkEffectMeta) {
            meta.effect = FireworkEffect.builder().withColor(color).build()
            item.itemMeta = meta
        }
        return this
    }

    fun setSkullSkin(value: String, signature: String): ItemBuilder {
        if (item.type != Material.SKULL_ITEM) return this
        val gameProfile = GameProfile(UUID.randomUUID(), null)
        gameProfile.properties.put("textures", Property("textures", value, signature))

        val meta = item.itemMeta as? SkullMeta ?: return this
        try {
            val field: Field = meta.javaClass.getDeclaredField("profile")
            field.isAccessible = true
            field.set(meta, gameProfile)
            item.itemMeta = meta
        } catch (_: Exception) {
        }
        return this
    }

    fun addEnchant(enchant: Enchantment, level: Int): ItemBuilder {
        if (level >= 1) {
            item.itemMeta = item.itemMeta.apply { addEnchant(enchant, level, true) }
        }
        return this
    }

    fun unEnchant(enchant: Enchantment): ItemBuilder {
        item.removeEnchantment(enchant)
        return this
    }

    fun setLore(vararg lines: String): ItemBuilder {
        return setLore(lines.toList())
    }

    fun setLore(lines: List<String>?): ItemBuilder {
        if (lines != null) {
            val processed = lines
                .map(::color)
                .filter { it.isNotEmpty() }

            val meta = item.itemMeta
            meta.lore = processed
            item.itemMeta = meta
        }
        return this
    }

    fun addLore(lines: List<String>): ItemBuilder {
        lines.forEach(this::addLore)
        return this
    }

    fun addLore(line: String): ItemBuilder {
        val meta = item.itemMeta
        val lore = meta.lore?.toMutableList() ?: mutableListOf()
        lore.add(color(line))
        meta.lore = lore
        item.itemMeta = meta
        return this
    }

    fun addLore(index: Int, line: String): ItemBuilder {
        val meta = item.itemMeta
        val lore = meta.lore?.toMutableList() ?: mutableListOf()
        val clampedIndex = index.coerceIn(0, lore.size)
        lore.add(clampedIndex, color(line))
        meta.lore = lore
        item.itemMeta = meta
        return this
    }

    fun clearLore(): ItemBuilder {
        item.itemMeta = item.itemMeta.apply { lore = null }
        return this
    }

    fun addFlags(vararg flags: ItemFlag): ItemBuilder {
        item.itemMeta = item.itemMeta.apply { addItemFlags(*flags) }
        return this
    }

    fun removeFlags(vararg flags: ItemFlag): ItemBuilder {
        item.itemMeta = item.itemMeta.apply { removeItemFlags(*flags) }
        return this
    }

    fun removeAllFlags(): ItemBuilder {
        val meta = item.itemMeta
        meta.itemFlags.forEach { meta.removeItemFlags(it) }
        item.itemMeta = meta
        return this
    }

    fun hideEnchants(): ItemBuilder = addFlags(ItemFlag.HIDE_ENCHANTS)
    fun hideAttributes(): ItemBuilder = addFlags(ItemFlag.HIDE_ATTRIBUTES)
    fun hideEffects(): ItemBuilder = addFlags(ItemFlag.HIDE_POTION_EFFECTS)

    fun setColor(color: Color): ItemBuilder {
        if (item.type in setOf(
                Material.LEATHER_BOOTS,
                Material.LEATHER_CHESTPLATE,
                Material.LEATHER_HELMET,
                Material.LEATHER_LEGGINGS
            )) {
            val meta = item.itemMeta as? LeatherArmorMeta ?: return this
            meta.setColor(color)
            item.itemMeta = meta
        } else {
            throw IllegalArgumentException("color() only applicable for leather armor!")
        }
        return this
    }

    fun setNBTTag(tag: String, value: Any): ItemBuilder {
        val copyItem = CraftItemStack.asNMSCopy(item)
        val compound = copyItem.tag ?: NBTTagCompound()

        when (value) {
            is String -> compound.setString(tag, value)
            is Int -> compound.setInt(tag, value)
            is Boolean -> compound.setBoolean(tag, value)
        }

        copyItem.tag = compound
        return this
    }

    fun setupPrivateTracker(): ItemBuilder {
        return setLore("&8#${ThreadLocalRandom.current().nextInt(1000, 9999)}")
    }

    fun build(): ItemStack = item


    private fun color(input: String): String =
        ChatColor.translateAlternateColorCodes('&', input)
}