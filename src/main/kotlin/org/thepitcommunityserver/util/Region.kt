package org.thepitcommunityserver.util

import org.bukkit.Location

fun isInsideRegion(playLocation: Location, lowerBounds: Location, upperBounds: Location): Boolean {
    val minX = lowerBounds.x.coerceAtMost(upperBounds.x)
    val minY = lowerBounds.y.coerceAtMost(upperBounds.y)
    val minZ = lowerBounds.z.coerceAtMost(upperBounds.z)
    val maxX = lowerBounds.x.coerceAtLeast(upperBounds.x)
    val maxY = lowerBounds.y.coerceAtLeast(upperBounds.y)
    val maxZ = lowerBounds.z.coerceAtLeast(upperBounds.z)

    return playLocation.x in minX..maxX &&
            playLocation.y >= minY && playLocation.y <= maxY &&
            playLocation.z >= minZ && playLocation.z <= maxZ &&
            playLocation.world == lowerBounds.world && playLocation.world == upperBounds.world
}
