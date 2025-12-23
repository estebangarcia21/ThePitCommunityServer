package org.thepitcommunityserver.game.items.defaultItems

import org.bukkit.Material
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig

object Arrows : Item {
    override val config = ItemConfig(
        name = "Arrow",
        material = Material.ARROW,
        count = 32,
        flags = emptyList(),
        nbtTags = mapOf(),
    )
}
