package org.thepitcommunityserver.game.items.defaultItems

import org.bukkit.Material
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig
import org.thepitcommunityserver.util.NBT

object IronBoots : Item {
    override val config = ItemConfig(
        name = "Iron Boots",
        material = Material.IRON_BOOTS,
        unbreakable = true,
        flags = emptyList(),
        nbtTags = mapOf(
            NBT.AUTO_EQUIP.entry,
            NBT.LOSE_ON_DEATH.entry,
            NBT.AUTO_EQUIP_OVERRIDABLE.entry,
            NBT.DISABLE_PICKUP_EXISTS_IN_INVENTORY.entry,

            ),

        )
}