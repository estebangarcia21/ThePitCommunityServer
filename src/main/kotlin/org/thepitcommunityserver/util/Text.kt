package org.thepitcommunityserver.util

object Text {
    const val HEART = "❤"
    const val INFINITY = "∞"
}

fun formatPercentage(percent: Double?): String {
    return "${((percent ?: 0.0) * 100).toInt()}%"
}

fun intToRoman(num: Int?): String {
    require(num in 1..10) { "Input number must be between 1 and 5 (inclusive)." }

    val romanNumerals = mapOf(
        1 to "I",
        2 to "II",
        3 to "III",
        4 to "IV",
        5 to "V",
        6 to "VI",
        7 to "VII",
        8 to "VIII",
        9 to "IX",
        10 to "X"
    )

    return romanNumerals[num] ?: ""
}
