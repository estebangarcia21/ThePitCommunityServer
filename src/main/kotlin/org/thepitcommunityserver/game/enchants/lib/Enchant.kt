package org.thepitcommunityserver.game.enchants.lib

import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.game.enchants.*
import org.thepitcommunityserver.util.syncLoreWithEnchantments

val Enchants = listOf(
    Gamble,
    Mirror,
    DevilChicks,
    Chipping,
    BottomlessQuiver,
    BulletTime,
    Crush,
    Wasp,
    Parasite,
    Prick,
    MegaLongbow,
    Assassin,
    Peroxide,
    SpeedyHit,
    BatPack,
    SuperMonkey,
    Telebow,
    LastStand,
    Executioner,
    Healer,
    Knockback,
    SprintDrain,
    Perun,
    ComboSwift,
    ComboStun,
    ComboHeal,
    CounterJanitor,
    SpeedyKill,
    Volley,
    PushComesToShove,
    PinDown,
    WhatDoesntKillYou,
    Explosive
)

interface Enchant : Listener {
    val config: EnchantConfig
}

enum class EnchantGroup {
    A,
    B,
    C
}

enum class EnchantType {
    BOW,
    SWORD,
    PANTS
}

typealias EnchantDescription = (tier: Int) -> String

data class EnchantConfig(
    val name: String,
    val tiers: List<Int>,
    val group: EnchantGroup,
    val rare: Boolean,
    val type: EnchantType,
    val description: EnchantDescription,
)

fun enchantByName(name: String): Enchant? {
    return Enchants.find { it.config.name.equals(name, ignoreCase = true) }
}

/**
 * Gets enchantments in `Name -> Level` manner.
 */
fun getItemMysticEnchantments(item: ItemStack?): Map<String, Int>? {
    if (item == null) return emptyMap()

    val nmsItemStack = CraftItemStack.asNMSCopy(item) ?: return null

    val compound = nmsItemStack.tag ?: return emptyMap()

    val enchantments = linkedMapOf<String, Int>()
    val enchantmentCompound = compound.getCompound("MysticEnchantments")

    // Read order from a separate string list
    val orderString = compound.getString("MysticEnchantmentOrder")
    val orderedKeys = if (orderString.isNotEmpty()) orderString.split(",") else enchantmentCompound.c().toList()

    for (key in orderedKeys) {
        if (enchantmentCompound.hasKey(key)) {
            enchantments[key] = enchantmentCompound.getInt(key)
        }
    }

    return enchantments
}

fun setItemMysticEnchantments(item: ItemStack?, enchantments: Map<String, Int>) {
    if (item == null) return

    val nmsItemStack = CraftItemStack.asNMSCopy(item)
    val compound = nmsItemStack.tag ?: NBTTagCompound()

    val enchantmentCompound = NBTTagCompound()

    for ((key, value) in enchantments) {
        enchantmentCompound.setInt(key, value)
    }

    compound.set("MysticEnchantments", enchantmentCompound)

    compound.setString("MysticEnchantmentOrder", enchantments.keys.joinToString(","))

    nmsItemStack.tag = compound
    item.itemMeta = CraftItemStack.getItemMeta(nmsItemStack)

    syncLoreWithEnchantments(item)
}

/**
 * Returns -1 if the item does not have an enchant.
 */
fun getEnchantTierForItem(enchant: Enchant, item: ItemStack?): Int? {
    if (item == null) return null

    return getItemMysticEnchantments(item)?.get(enchant.config.name)
}
