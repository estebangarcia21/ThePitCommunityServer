package org.thepitcommunityserver.util

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
