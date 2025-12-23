package org.thepitcommunityserver.game.items.defaultItems

import org.bukkit.Material
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig
import org.thepitcommunityserver.util.NBT

object IronSword : Item {
    override val config = ItemConfig(
        name = "Iron Sword",
        material = Material.IRON_SWORD,
        unbreakable = true,
        flags = emptyList(),
        nbtTags = mapOf(
            NBT.LOSE_ON_DEATH.entry,
            NBT.UNDROPPABLE.entry,
        ),
    )
}
