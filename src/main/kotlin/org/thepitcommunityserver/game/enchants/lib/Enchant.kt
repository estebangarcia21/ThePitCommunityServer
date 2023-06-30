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
    Knockback
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
    return Enchants.find { it.config.name.equals(name, ignoreCase = true)}
}

/**
 * Gets enchantments in `Name -> Level` manner.
 */
fun getItemMysticEnchantments(item: ItemStack?): Map<String, Int>? {
    if (item == null) return emptyMap()

    val nmsItemStack = CraftItemStack.asNMSCopy(item) ?: return null

    val compound = nmsItemStack.tag ?: return emptyMap()

    val enchantments = mutableMapOf<String, Int>()
    val enchantmentCompound = compound.getCompound("MysticEnchantments")
    val keySet = enchantmentCompound.c()

    for (key in keySet) {
        val value = enchantmentCompound.getInt(key)
        enchantments[key] = value
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
