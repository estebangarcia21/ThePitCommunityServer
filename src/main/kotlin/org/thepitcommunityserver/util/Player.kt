package org.thepitcommunityserver.util

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

val Player.leggings: ItemStack?
    get() = this.inventory.leggings

fun DeserializedLocation.toBukkitVector(): Vector {
    require(this.size == 3) { "List must be a of size 3 to convert to org.bukkit.util.vector (x, y, z)" }

    return Vector(this[0], this[1], this[2])
}
