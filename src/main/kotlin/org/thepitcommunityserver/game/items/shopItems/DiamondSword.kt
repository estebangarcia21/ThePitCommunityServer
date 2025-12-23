package org.thepitcommunityserver.game.items.shopItems

import org.bukkit.Material
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig

object DiamondSword : Item {
    override val config: ItemConfig
        get() = ItemConfig(
            name = "Diamond Sword",
            material = Material.DIAMOND_SWORD,
            unbreakable = true,
            flags = emptyList(),
        )
}