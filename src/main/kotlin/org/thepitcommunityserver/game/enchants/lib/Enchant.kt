package org.thepitcommunityserver.game.enchants.lib

import com.google.gson.Gson
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.game.enchants.DevilChicks
import org.thepitcommunityserver.game.enchants.Gamble
import org.thepitcommunityserver.game.enchants.Mirror

val Enchants = listOf(
    Gamble,
    Mirror,
    DevilChicks
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

data class EnchantContext(val tier: Number)

typealias TierAttributes = Map<String, String>

typealias EnchantDescription = (tier: Int) -> String

data class EnchantConfig(
    val name: String,
    val tiers: List<Int>,
    val group: EnchantGroup,
    val rare: Boolean,
    val type: EnchantType,
    val description: EnchantDescription,
)

/**
 * Gets enchantments in `Name -> Level` manner.
 */
fun getItemMysticEnchantments(item: ItemStack?): Map<String, Number> {
    if (item == null) return emptyMap()

    val nmsItemStack = CraftItemStack.asNMSCopy(item)
    val compound = nmsItemStack.tag ?: return emptyMap()

    val gson = Gson()
    val serializedJson = compound.getString("MysticEnchantments")

    @Suppress("UNCHECKED_CAST")
    return gson.fromJson(serializedJson, Map::class.java) as? Map<String, Double> ?: emptyMap()
}

/**
 * Sets enchantments in a `Name -> Level` format.
 */
fun setItemMysticEnchantments(item: ItemStack?, enchantments: Map<String, Number>) {
    if (item == null) return

    val nmsItemStack = CraftItemStack.asNMSCopy(item)
    val compound = nmsItemStack.tag ?: NBTTagCompound()

    val gson = Gson()
    val serializedJson = gson.toJson(enchantments)

    compound.setString("MysticEnchantments", serializedJson)
    nmsItemStack.tag = compound
    item.itemMeta = CraftItemStack.getItemMeta(nmsItemStack)
}

/**
 * Returns -1 if the item does not have an enchant.
 */
fun getEnchantTierForItem(enchant: Enchant, item: ItemStack?): Int {
    if (item == null) return NON_EXISTENT

    val enchantments = getItemMysticEnchantments(item)
    val tierValue = enchantments[enchant.config.name]

    return tierValue?.toInt() ?: -1
}

const val NON_EXISTENT = -1
