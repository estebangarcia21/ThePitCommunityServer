package org.thepitcommunityserver.game.items.shopItems

import org.bukkit.Material
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig
import org.thepitcommunityserver.util.NBT

object DiamondBoots : Item {
    override val config: ItemConfig
        get() = ItemConfig(
            name = "Diamond Boots",
            material = Material.DIAMOND_BOOTS,
            unbreakable = true,
            flags = emptyList(),
            nbtTags = mapOf(NBT.AUTO_EQUIP.entry, NBT.LOSE_ON_DEATH.entry)

        )
}
