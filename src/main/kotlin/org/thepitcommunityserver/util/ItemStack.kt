package org.thepitcommunityserver.util

import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import java.util.*

fun buildItem(
    material: Material,
    data: Byte? = 0,
    name: String? = null,
    itemColor: String? = null,
    lore: List<String> = emptyList(),
    count: Int = 1,
    unbreakable: Boolean = false,
    flags: List<ItemFlag> = emptyList(),
    nbtTags: DeserializedNBTMap = emptyMap(),
    overrideExistingNBTTags: Boolean = false,
    player: Player? = null, // Debugging can remove if needed but tbh might need in future
): ItemStack {
    var item = ItemStack(material, count, 0, data ?: 0)
    var lore = lore

    var itemMeta = item.itemMeta

    if (name != null) {
        val coloredName = if (itemColor != null) {
            "<reset><${itemColor}>${name}</${itemColor}>"
        } else {
            "<reset>${name}"
        }
        itemMeta.displayName = coloredName.parseChatColors()
    }

    itemMeta.addItemFlags(*flags.toTypedArray())
    if (unbreakable && !flags.contains(ItemFlag.HIDE_UNBREAKABLE)) {
        itemMeta.spigot().isUnbreakable = true
        itemMeta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE)
    }

    if (isLeather(material) && isLeggings(material)) {
        itemMeta = itemMeta as LeatherArmorMeta
        itemMeta.color = pantsColors(itemColor ?: "white")
    }

    item.itemMeta = itemMeta

    if (nbtTags.isNotEmpty()) {
        val builtNbtTags = buildNBTCompound(nbtTags)

        if (overrideExistingNBTTags) {
            item.nbt = builtNbtTags
        } else {
            item.nbt = mergeNBTCompounds(item.nbt, builtNbtTags)
        }
    }

    if (hasNBTEntryFor(item.nbt, NBT.KEPT_ON_DEATH.key)) {
        lore = listOf("<gray>Kept on death</gray>") + lore
    }
    if (lore.isNotEmpty()) {
        setItemLore(item, lore.map { it.parseChatColors() })
    }

    return item
}

private fun pitItemIdKey(signature: UUID) = "Pit:$signature/ItemID"

fun setItemID(signature: UUID, item: ItemStack, value: String) {
    val craftItemStack = CraftItemStack.asNMSCopy(item) ?: return

    val rootNBT = craftItemStack.tag ?: return
    rootNBT.setString(pitItemIdKey(signature), value)

    craftItemStack.tag = rootNBT
    item.itemMeta = CraftItemStack.getItemMeta(craftItemStack)
}

fun getItemID(signature: UUID, item: ItemStack): String? {
    val craftItemStack = CraftItemStack.asNMSCopy(item) ?: return null
    val rootNBT = craftItemStack.tag ?: return null

    return rootNBT.getString(pitItemIdKey(signature))
}

// Will nee to overhaul
fun pantsColors(color: String): Color {
    return when (color.lowercase()) {
        "red" -> Color.fromRGB(255, 85, 85)
        "gold" -> Color.fromRGB(255, 170, 0)
        "yellow" -> Color.fromRGB(255, 255, 85)
        "green" -> Color.fromRGB(85, 255, 85)
        "blue" -> Color.fromRGB(85, 85, 255)

        "dark-aqua" -> Color.fromRGB(125, 195, 131)
        "dark-red" -> Color.fromRGB(120, 0, 0)
        "aqua" -> Color.fromRGB(85, 255, 255)

        "dark-purple" -> Color.BLACK

        // Extra
        "brown" -> Color.fromRGB(150, 75, 0)
        "gray", "grey" -> Color.GRAY
        "lime" -> Color.LIME
        "magenta" -> Color.FUCHSIA
        "purple" -> Color.PURPLE
        "silver" -> Color.SILVER
        "white" -> Color.WHITE
        "maroon" -> Color.MAROON
        "navy" -> Color.NAVY
        "olive" -> Color.OLIVE
        "teal" -> Color.TEAL
        else -> Color.WHITE
    }
}
