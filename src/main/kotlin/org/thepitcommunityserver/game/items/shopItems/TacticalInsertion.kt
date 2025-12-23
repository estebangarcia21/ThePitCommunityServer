package org.thepitcommunityserver.game.items.shopItems

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig


object TacticalInsertion : Item {
    override val config: ItemConfig
        get() = ItemConfig(
            name = "Tactical Insertion",
            itemColor = "yellow",
            lore = listOf(
                "Sets your next spawnpoint where",
                "you stand.",
            ),
            material = Material.BLAZE_ROD,
        )

    private val description = ""

    @EventHandler
    fun onRightClick(event: PlayerInteractEvent) {

    }
}