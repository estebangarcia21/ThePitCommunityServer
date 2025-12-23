package org.thepitcommunityserver.game.items.shopItems

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig

object GoldenPickaxe : Item {
    override val config: ItemConfig
        get() = ItemConfig(
            name = "Golden Pickaxe",
            material = Material.GOLD_PICKAXE,
            lore = listOf(
                "Breaks a 5-high pillar of",
                "obsidian when 2-tapping it"
            ),
            itemColor = "gold",
            unbreakable = true,
            flags = emptyList(),
        )

    @EventHandler
    fun onLeftClickBlock(event: PlayerInteractEvent) {

    }
}