package org.thepitcommunityserver.game.items.shopItems

import org.bukkit.Material
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig
import org.thepitcommunityserver.util.NBT

object DiamondLeggings : Item {
    override val config: ItemConfig
        get() = ItemConfig(
            name = "Diamond Leggings",
            material = Material.DIAMOND_LEGGINGS,
            unbreakable = true,
            flags = emptyList(),
            nbtTags = mapOf(NBT.AUTO_EQUIP.entry, NBT.LOSE_ON_DEATH.entry)

        )
}