package org.thepitcommunityserver.util

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

inline fun Player.asCraftPlayer(block: (craftPlayer: CraftPlayer) -> Unit) {
    if (this is CraftPlayer) {
        block(this)
    }
}
