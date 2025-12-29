package org.thepitcommunityserver.game.items.defaultItems

import org.bukkit.Material
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig
import org.thepitcommunityserver.util.NBT

object ChainmailLeggings : Item {
    override val config = ItemConfig(
        name = "Chainmail Leggings",
        material = Material.CHAINMAIL_LEGGINGS,
        unbreakable = true,
        flags = emptyList(),
        nbtTags = mapOf(
            NBT.AUTO_EQUIP.entry,
            NBT.LOSE_ON_DEATH.entry,
            NBT.REMOVE_ON_DROP.entry,
            NBT.AUTO_EQUIP_OVERRIDABLE.entry,
        ),
    )
}
