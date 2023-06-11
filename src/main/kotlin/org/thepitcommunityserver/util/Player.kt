package org.thepitcommunityserver.util

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

val Player.leggings: ItemStack?
    get() = this.inventory.leggings

fun listToLocation(world: World, list: List<Double>): Location {
    require(list.size == 3) { "Array must be a of size 3! (x, y, z)" }

    return Location(world, list[0], list[1], list[2])
}
