package org.thepitcommunityserver.util

import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*

fun buildItem(
    material: Material,
    name: String? = null,
    lore: List<String> = emptyList(),
    count: Int = 1,
    unbreakable: Boolean = false,
    flags: List<ItemFlag> = emptyList(),
    nbtTags: DeserializedNBTMap = emptyMap(),
    overrideExistingNBTTags: Boolean = false
): ItemStack {
    val item = ItemStack(material, count)

    if (lore.isNotEmpty()) {
        setItemLore(item, lore)
    }

    val itemMeta = item.itemMeta

    if (name != null) itemMeta.displayName = name
    if (unbreakable) itemMeta.spigot().isUnbreakable = true

    itemMeta.addItemFlags(*flags.toTypedArray())

    item.itemMeta = itemMeta

    if (nbtTags.isNotEmpty()) {
        val builtNbtTags = buildNBTCompound(nbtTags)

        if (overrideExistingNBTTags) {
            item.nbt = builtNbtTags
        } else {
            item.nbt = mergeNBTCompounds(item.nbt, builtNbtTags)
        }
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
