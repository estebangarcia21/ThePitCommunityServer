package org.thepitcommunityserver.util

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.db.data
import org.thepitcommunityserver.game.enchants.lib.isEmptyItemStack

val Player.leggings: ItemStack?
    get() = this.inventory.leggings

fun addItemToInventoryEmptySlot(inventory: Inventory, item: ItemStack, preferredSlot: Int? = null) {
    val contents = inventory.contents

    if (preferredSlot != null) {
        if (isEmptyItemStack(inventory.getItem(preferredSlot))) {
            inventory.setItem(preferredSlot, item)
            return
        }
    }

    for (i in contents.indices) {
        val slotItem: ItemStack? = contents[i]
        if (isEmptyItemStack(slotItem)) {
            inventory.setItem(i, item)
            break
        }
    }
}

fun findFirstEmptySlot(inventory: Inventory, skipHotbar: Boolean = false): Int {
    val start = if (skipHotbar) 9 else 0

    for (i in start..<inventory.size) {
        if (isEmptyItemStack(inventory.getItem(i))) {
            return i
        }
    }

    // Check hotbar as fallback if we skipped it
    if (skipHotbar) {
        for (i in 0..<9) {
            if (isEmptyItemStack(inventory.getItem(i))) {
                return i
            }
        }
    }

    return -1
}

fun addItemToPlayerInventory(player: Player, item: ItemStack?): Boolean {
    val slot = findFirstEmptySlot(player.inventory, skipHotbar = true)
    if (slot >= 0) {
        player.inventory.setItem(slot, item)
        return true
    }
    return false
}

fun playerHasItem(player: Player, material: Material): Boolean {
    val inventory = player.inventory

    // Check armor slots
    val hasEquipped = when {
        isHelmet(material) -> inventory.helmet?.type == material
        isChestplate(material) -> inventory.chestplate?.type == material
        isLeggings(material) -> inventory.leggings?.type == material
        isBoots(material) -> inventory.boots?.type == material
        else -> false
    }

    if (!hasEquipped) {
        return false
    }

    // Check main inventory
    return inventory.contents.any { it?.type == material }
}

fun formatPitPlayerName(player: Player): String {
    val playerData = player.data

    return "${formatBracketsForLevel(playerData.level, playerData.prestige)} ${player.name}"
}
