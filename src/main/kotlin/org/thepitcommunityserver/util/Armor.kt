package org.thepitcommunityserver.util

import org.bukkit.Material

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
