package org.thepitcommunityserver.util

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.game.enchants.lib.isEmptyItemStack

val Player.leggings: ItemStack?
    get() = this.inventory.leggings

fun addItemToInventoryEmptySlot(inventory: Inventory, item: ItemStack) {
    val contents = inventory.contents

    for (i in contents.indices) {
        val slotItem: ItemStack? = contents[i]
        if (isEmptyItemStack(slotItem)) {
            inventory.setItem(i, item)
            break
        }
    }
}
