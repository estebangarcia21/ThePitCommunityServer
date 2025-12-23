package org.thepitcommunityserver.game.items.defaultItems

import org.bukkit.Material
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig
import org.thepitcommunityserver.util.NBT

object Bow : Item {
    override val config = ItemConfig(
        name = "Bow",
        material = Material.BOW,
        unbreakable = true,
        flags = emptyList(),
        nbtTags = mapOf(
            NBT.LOSE_ON_DEATH.entry,
            NBT.UNDROPPABLE.entry,
        ),
    )
}
