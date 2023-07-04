package org.thepitcommunityserver.util

import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.game.enchants.lib.Enchant
import org.thepitcommunityserver.game.enchants.lib.enchantByName
import org.thepitcommunityserver.game.enchants.lib.getItemMysticEnchantments
import java.util.*

fun syncLoreWithEnchantments(item: ItemStack?) {
    val enchants = getItemMysticEnchantments(item) ?: return

    val lore = arrayListOf("""
        <gray>Lives: </gray><green>${Text.INFINITY}</green>/<gray>${Text.INFINITY}</green>
    """.trimIndent(), "")

    enchants.entries.forEach { (name, tier) -> enchantByName(name)?.let {
        enchant -> lore += getEnchantLore(enchant, tier) + ""
    } }

    // Remove the trailing line break in the lore.
    lore.removeLast()

    setItemLore(item, lore.map(::replaceChatColorTags))
}

fun getEnchantLore(enchant: Enchant, tier: Int): List<String> {
    return listOf(formatEnchantmentTitle(enchant, tier)) + parseDescriptionLore(enchant, tier)
}

fun formatEnchantmentTitle(enchant: Enchant, tier: Int): String {
    val rarityPrefix = if (enchant.config.rare) "${ChatColor.LIGHT_PURPLE}RARE! " else ""
    val levelSuffix = if (tier != 1) " ${intToRoman(tier)}" else ""

    return "$rarityPrefix${ChatColor.BLUE}${enchant.config.name}$levelSuffix"
}

fun parseDescriptionLore(enchant: Enchant, tier: Int): List<String> {
    val acc = arrayListOf<String>()
    val lore = enchant.config.description(tier)
    val lines = lore.split("<br/>")

    lines.forEach { l ->
        val line = replaceChatColorTags(l, defaultColor = ChatColor.GRAY)

        acc.add(line)
    }

    return acc
}

fun replaceChatColorTags(string: String, defaultColor: ChatColor = ChatColor.GRAY): String {
    var acc = defaultColor.toString() + string

    ChatColor.values().forEach { c ->
        val name = c.name.lowercase(Locale.getDefault()).replace("_", "-")

        acc = acc.replace("<$name>", c.toString())
        acc = acc.replace("</$name>", defaultColor.toString())

        acc = acc.replace("<$name:bold>", c.toString() + ChatColor.BOLD.toString())
        acc = acc.replace("</$name:bold>",ChatColor.RESET.toString() + defaultColor.toString())

        acc += defaultColor.toString()
    }

    return acc
}

fun buildLore(vararg lines: String, defaultColor: ChatColor = ChatColor.GRAY): List<String> {
    return lines.map(::replaceChatColorTags)
}

fun String.parseChatColors(): String {
    return replaceChatColorTags(this)
}

fun setItemLore(item: ItemStack?, lore: List<String>) {
    if (item == null) return

    val meta = item.itemMeta
    meta.lore = lore

    item.itemMeta = meta
}
