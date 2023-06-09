package org.thepitcommunityserver.util

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class XPTest {
    // From https://docs.google.com/spreadsheets/d/1ZnG7kv3dTZhsowr-8vK3kiJCFT-w-vJzY5lhJqLSSmc/edit#gid=0
    private val expectedXPValues = listOf(
        listOf(0, 15, 30, 50, 75, 125, 300, 600, 800, 900, 1000, 1200, 1500, 0),
        listOf(1, 17, 33, 55, 85, 140, 330, 660, 880, 990, 1100, 1320, 1650, 0),
        listOf(2, 18, 36, 60, 90, 150, 360, 720, 960, 1080, 1200, 1440, 1800, 0),
        listOf(3, 20, 39, 65, 100, 165, 390, 780, 1040, 1170, 1300, 1560, 1950, 0),
        listOf(4, 21, 42, 70, 105, 175, 420, 840, 1120, 1260, 1400, 1680, 2100, 0),
        listOf(5, 23, 45, 75, 115, 190, 450, 900, 1200, 1350, 1500, 1800, 2250, 0),
        listOf(6, 27, 55, 88, 131, 218, 525, 1050, 1400, 1575, 1750, 2100, 2625, 0),
        listOf(7, 30, 60, 100, 150, 250, 600, 1200, 1600, 1800, 2000, 2400, 3000, 0),
        listOf(8, 38, 75, 125, 188, 313, 750, 1500, 2000, 2250, 2500, 3000, 3750, 0),
        listOf(9, 45, 90, 150, 225, 375, 900, 1800, 2400, 2700, 3000, 3600, 4500, 0),
        listOf(10, 60, 120, 200, 300, 500, 1200, 2400, 3200, 3600, 4000, 4800, 6000, 0),
        listOf(11, 75, 150, 250, 375, 625, 1500, 3000, 4000, 4500, 5000, 6000, 7500, 0),
        listOf(12, 90, 180, 300, 450, 750, 1800, 3600, 4800, 5400, 6000, 7200, 9000, 0),
        listOf(13, 105, 210, 350, 525, 875, 2100, 4200, 5600, 6300, 7000, 8400, 10500, 0),
        listOf(14, 120, 240, 400, 600, 1000, 2400, 4800, 6400, 7200, 8000, 9600, 12000, 0),
        listOf(15, 135, 270, 450, 675, 1125, 2700, 5400, 7200, 8100, 9000, 10800, 13500, 0),
        listOf(16, 150, 300, 500, 750, 1250, 3000, 6000, 8000, 9000, 10000, 12000, 15000, 0),
        listOf(17, 180, 360, 600, 900, 1500, 3600, 7200, 9600, 10800, 12000, 14400, 18000, 0),
        listOf(18, 210, 420, 700, 1050, 1750, 4200, 8400, 11200, 12600, 14000, 16800, 21000, 0),
        listOf(19, 240, 480, 800, 1200, 2000, 4800, 9600, 12800, 14400, 16000, 19200, 24000, 0),
        listOf(20, 270, 540, 900, 1350, 2250, 5400, 10800, 14400, 16200, 18000, 21600, 27000, 0),
        listOf(21, 300, 600, 1000, 1500, 2500, 6000, 12000, 16000, 18000, 20000, 24000, 30000, 0),
        listOf(22, 360, 720, 1200, 1800, 3000, 7200, 14400, 19200, 21600, 24000, 28800, 36000, 0),
        listOf(23, 420, 840, 1400, 2100, 3500, 8400, 16800, 22400, 25200, 28000, 33600, 42000, 0),
        listOf(24, 480, 960, 1600, 2400, 4000, 9600, 19200, 25600, 28800, 32000, 38400, 48000, 0),
        listOf(25, 540, 1080, 1800, 2700, 4500, 10800, 21600, 28800, 32400, 36000, 43200, 54000, 0),
        listOf(26, 600, 1200, 2000, 3000, 5000, 12000, 24000, 32000, 36000, 40000, 48000, 60000, 0),
        listOf(27, 675, 1350, 2250, 3375, 5625, 13500, 27000, 36000, 40500, 45000, 54000, 67500, 0),
        listOf(28, 750, 1500, 2500, 3750, 6250, 15000, 30000, 40000, 45000, 50000, 60000, 75000, 0),
        listOf(29, 1125, 2250, 3750, 5625, 9375, 22500, 45000, 60000, 67500, 75000, 90000, 112500, 0),
        listOf(30, 1500, 3000, 5000, 7500, 12500, 30000, 60000, 80000, 90000, 100000, 120000, 150000, 0)
    )

    @Test
    fun testRequiredXPForLevel() {
        val marginOfError = 5

        for (row in expectedXPValues) {
            val prestige = row[0]
            for (i in 1 until row.size) {
                var level = (i - 1) * 10
                if (level == 0) level = 1

                val expectedXP = row[i]
                val actualXP = getRequiredXPForLevel(level, prestige)
                val errorMessage = "Prestige: $prestige; Level: $level; Actual XP: $actualXP, Expected Range: ${expectedXP - marginOfError} to ${expectedXP + marginOfError} (Real is $expectedXP)"

                assertTrue(actualXP in (expectedXP - marginOfError)..(expectedXP + marginOfError), errorMessage)
            }
        }
    }
}
