package org.thepitcommunityserver.util

import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.game.enchants.lib.Enchant
import java.util.*

fun applyEnchantmentLore(item: ItemStack?, enchant: Enchant, tier: Int) {
    if (item == null) return

    val lore = parseLore(enchant.config.description(tier))
    val title = formatEnchantName(enchant.config.name, enchant.config.rare, tier)

    val finalLore = listOf(title) + lore

    val meta = item.itemMeta
    meta.lore = finalLore

    item.itemMeta = meta
}

fun formatEnchantName(name: String?, isRare: Boolean, tier: Int): String {
    val rarityPrefix = if (isRare) "${ChatColor.LIGHT_PURPLE}RARE! " else ""
    val levelSuffix = if (tier != 1) " ${intToRoman(tier)}" else ""

    return "$rarityPrefix${ChatColor.BLUE}$name$levelSuffix"
}


fun parseLore(lore: String): List<String> {
    val acc = arrayListOf<String>()
    val lines = lore.split("<br/>")

    lines.forEach { l ->
        var line = ChatColor.GRAY.toString() + l

        ChatColor.values().forEach { c ->
            val name = c.name.lowercase(Locale.getDefault()).replace("_", "-")

            line = line.replace("<$name>", c.toString())
            line = line.replace("</$name>", ChatColor.GRAY.toString())
        }

        acc.add(line)
    }

    return acc
}

fun updateEnchantmentLore(item: ItemStack?, lore: List<String>) {
    if (item == null) return

    val meta = item.itemMeta
    meta.lore = lore

    item.itemMeta = meta
}
