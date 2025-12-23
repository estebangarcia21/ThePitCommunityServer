package org.thepitcommunityserver.game.items.shopItems

import org.bukkit.Material
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig

object ObsidianStack : Item {
    override val config: ItemConfig
        get() = ItemConfig(
            name = "Obsidian",
            material = Material.OBSIDIAN,
            count = 64
        )
}