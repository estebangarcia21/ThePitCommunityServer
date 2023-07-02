package org.thepitcommunityserver.util

import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*

fun buildItem(
    name: String,
    material: Material,
    itemID: String,
    lore: List<String> = emptyList(),
    count: Int = 1,
    flags: List<ItemFlag> = emptyList()
): ItemStack {
    val item = ItemStack(material, count)

    if (lore.isNotEmpty()) {
        setItemLore(item, lore)
    }

    val itemMeta = item.itemMeta
    itemMeta.displayName = name
    itemMeta.addItemFlags(*flags.toTypedArray())

    item.itemMeta = itemMeta

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
