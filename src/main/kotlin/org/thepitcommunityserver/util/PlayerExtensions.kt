package org.thepitcommunityserver.util

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

val Player.leggings: ItemStack
    get() = this.inventory.leggings
