package org.thepitcommunityserver.game.experience.XP

fun getXpForLevel(level: Int) : Int {
    return when (level) {
        in 0..9 -> 15
        in 10..19 -> 30
        in 20..29 -> 50
        in 30..39 -> 75
        in 40..49 -> 125
        in 50..59 -> 300
        in 60..69 -> 600
        in 70..79 -> 800
        in 80..89 -> 900
        in 90..99 -> 1000
        in 100..109 -> 1200
        in 110..119 -> 1500
        120 -> 0
        else -> 0 // Default Value
    }
}

fun getXPMultiplierForPrestige(prestige: Int) : Double{
    return when (prestige) {
        0 -> 1.0
        1 -> 1.1
        2 -> 1.2
        3 -> 1.3
        4 -> 1.4
        5 -> 1.5
        6 -> 1.75
        7 -> 2.0
        8 -> 2.5
        9 -> 3.0
        10 -> 4.0
        11 -> 5.0
        12 -> 6.0
        13 -> 7.0
        14 -> 8.0
        15 -> 9.0
        16 -> 10.0
        17 -> 12.0
        18 -> 14.0
        19 -> 16.0
        20 -> 18.0
        21 -> 20.0
        22 -> 24.0
        23 -> 28.0
        24 -> 32.0
        25 -> 36.0
        26 -> 40.0
        27 -> 45.0
        28 -> 50.0
        29 -> 75.0
        30 -> 100.0
        else -> {0.0}
    }
}

