package org.thepitcommunityserver.util

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.game.enchants.lib.isEmptyItemStack

fun isChestplate(material: Material): Boolean {
    return material == Material.LEATHER_CHESTPLATE || material == Material.CHAINMAIL_CHESTPLATE ||
            material == Material.IRON_CHESTPLATE || material == Material.GOLD_CHESTPLATE ||
            material == Material.DIAMOND_CHESTPLATE
}

fun isLeggings(material: Material): Boolean {
    return material == Material.LEATHER_LEGGINGS || material == Material.CHAINMAIL_LEGGINGS ||
            material == Material.IRON_LEGGINGS || material == Material.GOLD_LEGGINGS ||
            material == Material.DIAMOND_LEGGINGS
}

fun isBoots(material: Material): Boolean {
    return material == Material.LEATHER_BOOTS || material == Material.CHAINMAIL_BOOTS ||
            material == Material.IRON_BOOTS || material == Material.GOLD_BOOTS ||
            material == Material.DIAMOND_BOOTS
}

fun isHelmet(material: Material): Boolean {
    return material == Material.LEATHER_HELMET || material == Material.CHAINMAIL_HELMET ||
            material == Material.IRON_HELMET || material == Material.GOLD_HELMET ||
            material == Material.DIAMOND_HELMET
}

fun isArmor(material: Material): Boolean {
    return isHelmet(material) || isChestplate(material) ||
            isLeggings(material) || isBoots(material)
}

fun getEquippedArmorPiece(player: Player, material: Material): ItemStack? {
    val inventory = player.inventory
    return when {
        isHelmet(material) -> inventory.helmet
        isChestplate(material) -> inventory.chestplate
        isLeggings(material) -> inventory.leggings
        isBoots(material) -> inventory.boots
        else -> null
    }
}

fun getEquippedArmorPiece(player: Player, slot: org.thepitcommunityserver.game.guis.EquipSlot): ItemStack? {
    return when (slot) {
        _root_ide_package_.org.thepitcommunityserver.game.guis.EquipSlot.HELMET -> player.inventory.helmet
        _root_ide_package_.org.thepitcommunityserver.game.guis.EquipSlot.CHESTPLATE -> player.inventory.chestplate
        _root_ide_package_.org.thepitcommunityserver.game.guis.EquipSlot.LEGGINGS -> player.inventory.leggings
        _root_ide_package_.org.thepitcommunityserver.game.guis.EquipSlot.BOOTS -> player.inventory.boots
    }
}

fun setArmorPiece(player: Player, slot: org.thepitcommunityserver.game.guis.EquipSlot, item: ItemStack) {
    when (slot) {
        _root_ide_package_.org.thepitcommunityserver.game.guis.EquipSlot.HELMET -> player.inventory.helmet = item
        _root_ide_package_.org.thepitcommunityserver.game.guis.EquipSlot.CHESTPLATE -> player.inventory.chestplate =
            item

        _root_ide_package_.org.thepitcommunityserver.game.guis.EquipSlot.LEGGINGS -> player.inventory.leggings = item
        _root_ide_package_.org.thepitcommunityserver.game.guis.EquipSlot.BOOTS -> player.inventory.boots = item
    }
}

fun equipArmorPiece(player: Player, newPiece: ItemStack, currentPiece: ItemStack?) {
    val inventory = player.inventory
    val material = newPiece.type

    when {
        isHelmet(material) -> inventory.helmet = newPiece.clone()
        isChestplate(material) -> inventory.chestplate = newPiece.clone()
        isLeggings(material) -> inventory.leggings = newPiece.clone()
        isBoots(material) -> inventory.boots = newPiece.clone()
    }

    if (!isEmptyItemStack(currentPiece)) {
        addItemToPlayerInventory(player, currentPiece)
    }
}

fun isArmorPieceStronger(player: Player, material: Material): Boolean {

    val currentArmorPiece = when {
        isHelmet(material) -> player.inventory.helmet
        isChestplate(material) -> player.inventory.chestplate
        isLeggings(material) -> player.inventory.leggings
        isBoots(material) -> player.inventory.boots
        else -> return false
    }

    val armorRanking = listOf(
        Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
        Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
        Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS,
        Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
        Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS
    )

    val currentIndex = armorRanking.indexOf(currentArmorPiece?.type)
    val newIndex = armorRanking.indexOf(material)

    return newIndex > currentIndex
}
