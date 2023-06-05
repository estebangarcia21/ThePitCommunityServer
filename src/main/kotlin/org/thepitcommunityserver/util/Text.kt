package org.thepitcommunityserver.util

object Text {
    const val HEART = "❤"
}

fun formatPercentage(percent: Double?): String {
    return "${((percent ?: 0.0) * 100).toInt()}%"
}
