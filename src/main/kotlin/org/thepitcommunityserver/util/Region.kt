package org.thepitcommunityserver.util

import org.bukkit.Location
import org.bukkit.util.Vector

fun isInsideRegion(pos: Location, lowerBounds: Vector, upperBounds: Vector): Boolean {
    return isInsideRegion(pos.toVector(), lowerBounds, upperBounds)
}

fun isInsideRegion(pos: Vector, lowerBounds: Vector, upperBounds: Vector): Boolean {
    val minX = lowerBounds.x.coerceAtMost(upperBounds.x)
    val minY = lowerBounds.y.coerceAtMost(upperBounds.y)
    val minZ = lowerBounds.z.coerceAtMost(upperBounds.z)
    val maxX = lowerBounds.x.coerceAtLeast(upperBounds.x)
    val maxY = lowerBounds.y.coerceAtLeast(upperBounds.y)
    val maxZ = lowerBounds.z.coerceAtLeast(upperBounds.z)

    return pos.x in minX..maxX && pos.y >= minY && pos.y <= maxY && pos.z >= minZ && pos.z <= maxZ
}

fun isInsideSpawn(pos: Location): Boolean {
    return isInsideSpawn(pos.toVector())
}

fun isInsideSpawn(pos: Vector): Boolean {
    return isInsideRegion(
        pos,
        currentMap.bounds.spawn.lower.toBukkitVector(),
        currentMap.bounds.spawn.upper.toBukkitVector()
    )
}
