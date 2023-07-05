package org.thepitcommunityserver.util

import org.bukkit.ChatColor
import kotlin.math.ceil

fun getRequiredXPForLevel(level: Int, prestige: Int): Int {
    val baseXP = getXPBaseForLevel(level)
    if (prestige == 0) return baseXP.toInt()

    val prestigeModifier = getPrestigeModifier(prestige)
    val modifiedXP = baseXP + baseXP * (prestigeModifier / 100.0)
    return ceil(modifiedXP).toInt()
}

fun getXPBaseForLevel(level: Int): Double {
    return when (level) {
        in 1..9 -> 15.0
        in 10..19 -> 30.0
        in 20..29 -> 50.0
        in 30..39 -> 75.0
        in 40..49 -> 125.0
        in 50..59 -> 300.0
        in 60..69 -> 600.0
        in 70..79 -> 800.0
        in 80..89 -> 900.0
        in 90..99 -> 1000.0
        in 100..109 -> 1200.0
        in 110..119 -> 1500.0
        120 -> 0.0
        else -> 0.0
    }
}

fun getChatColorForLevel(level: Int): ChatColor {
    return when (level) {
        in 1..9 -> ChatColor.GRAY
        in 10..19 -> ChatColor.BLUE
        in 20..29 -> ChatColor.DARK_AQUA
        in 30..39 -> ChatColor.DARK_GREEN
        in 40..49 -> ChatColor.GREEN
        in 50..59 -> ChatColor.YELLOW
        in 60..69 -> ChatColor.GOLD
        in 70..79 -> ChatColor.RED
        in 80..89 -> ChatColor.DARK_RED
        in 90..99 -> ChatColor.DARK_PURPLE
        in 100..109 -> ChatColor.LIGHT_PURPLE
        in 110..119 -> ChatColor.WHITE
        120 -> ChatColor.AQUA
        else -> ChatColor.GRAY
    }
}

fun getPrestigeColor(prestigeLevel: Int): ChatColor {
    return when {
        prestigeLevel == 0 -> ChatColor.GRAY
        prestigeLevel in 1..4 -> ChatColor.BLUE
        prestigeLevel in 5..9 -> ChatColor.YELLOW
        prestigeLevel in 10..14 -> ChatColor.GOLD
        prestigeLevel in 15..19 -> ChatColor.RED
        prestigeLevel in 20..24 -> ChatColor.LIGHT_PURPLE
        prestigeLevel in 25..29 -> ChatColor.DARK_PURPLE
        prestigeLevel >= 30 -> ChatColor.WHITE
        else -> ChatColor.WHITE // Default to white for negative or invalid prestige levels
    }
}

fun getPrestigeModifier(prestigeLevel: Int): Double {
    val modifiers = listOf(
        0.0, 10.0, 20.0, 30.0, 40.0, 50.0,
        75.0, 100.0, 150.0, 200.0, 300.0,
        400.0, 500.0, 600.0, 700.0, 800.0,
        900.0, 1100.0, 1300.0, 1500.0, 1700.0,
        1900.0, 2300.0, 2700.0, 3100.0, 3500.0,
        3900.0, 4400.0, 4900.0, 7400.0, 9900.0
    )
    val index = prestigeLevel.coerceIn(0, 30)
    return modifiers[index]
}

fun formatLevel(level: Int, prestigeLevel: Int): String {
    val prestigeColor = getPrestigeColor(prestigeLevel)
    val chatColor = getChatColorForLevel(level)
    val formattedLevel = if (level >= 60) "${ChatColor.BOLD}$level" else level.toString()

    return "${ChatColor.RESET}${prestigeColor}[${chatColor}$formattedLevel${prestigeColor}]"
}
