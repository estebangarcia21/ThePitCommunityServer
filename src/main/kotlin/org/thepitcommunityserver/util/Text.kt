package org.thepitcommunityserver.util

object Text {
    const val HEART = "❤"
    const val INFINITY = "∞"
}

fun formatPercentage(percent: Double?): String {
    return "${((percent ?: 0.0) * 100).toInt()}%"
}

private val romanNumerals = mapOf(
    1 to "I",
    2 to "II",
    3 to "III",
    4 to "IV",
    5 to "V",
    6 to "VI",
    7 to "VII",
    8 to "VIII",
    9 to "IX",
    10 to "X",
    20 to "XX",
    30 to "XXX",
    40 to "XL",
    50 to "L"
)

fun intToRoman(num: Int?): String {
    require(num in 1..50) { "Input number must be between 1 and 50 (inclusive)." }

    return romanNumerals[num] ?: buildRomanNumeral(num)
}

private fun buildRomanNumeral(num: Int?): String {
    val tensDigit = num!! / 10 * 10
    val onesDigit = num % 10

    val tensNumeral = romanNumerals[tensDigit] ?: ""
    val onesNumeral = romanNumerals[onesDigit] ?: ""

    return tensNumeral + onesNumeral
}
