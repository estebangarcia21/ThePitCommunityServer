package org.thepitcommunityserver.game.enchants.lib

import com.google.gson.Gson
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

interface Enchant : Listener {
    val config: EnchantConfig
}

enum class EnchantGroup {
    A,
    B,
    C
}

data class EnchantContext(val tier: Number)

typealias TierAttributes = Map<String, String>

data class EnchantConfig(
    val name: String,
    val tiers: List<Int>,
    val group: EnchantGroup,
    val rare: Boolean,
    val description: (tier: Int) -> String,
)

/**
 * Gets enchantments in `Name -> Level` manner.
 */
fun getItemMysticEnchantments(item: ItemStack): Map<String, Int> {
    val nmsItemStack = CraftItemStack.asNMSCopy(item)
    val compound = nmsItemStack.tag ?: return emptyMap()

    val gson = Gson()
    val serializedJson = compound.getString("MysticEnchantments")

    @Suppress("UNCHECKED_CAST")
    return gson.fromJson(serializedJson, Map::class.java) as? Map<String, Int> ?: emptyMap()
}

/**
 * Sets enchantments in a `Name -> Level` format.
 */
fun setItemMysticEnchantments(item: ItemStack, enchantments: Map<String, Int>) {
    val nmsItemStack = CraftItemStack.asNMSCopy(item)
    val compound = nmsItemStack.tag ?: NBTTagCompound()

    val gson = Gson()
    val serializedJson = gson.toJson(enchantments)

    compound.setString("MysticEnchantments", serializedJson)
    nmsItemStack.tag = compound
    item.itemMeta = CraftItemStack.getItemMeta(nmsItemStack)
}

fun hasEnchantInItem(enchant: Enchant, item: ItemStack): Boolean {
    return getEnchantTierForItem(enchant, item) != NON_EXISTENT
}

/**
 * Returns -1 if the item does not have an enchant.
 */
fun getEnchantTierForItem(enchant: Enchant, item: ItemStack): Int {
    return getItemMysticEnchantments(item)[enchant.config.name] ?: -1
}

const val NON_EXISTENT = -1
