package org.thepitcommunityserver.util

object Text {
    const val HEART = "❤"
}

fun formatPercentage(percent: Double?): String {
    return "${((percent ?: 0.0) * 100).toInt()}%"
}

fun intToRoman(num: Int): String {
    require(num in 1..5) { "Input number must be between 1 and 5 (inclusive)." }

    val romanNumerals = mapOf(
        1 to "I",
        2 to "II",
        3 to "III",
        4 to "IV",
        5 to "V"
    )

    return romanNumerals[num] ?: ""
}
