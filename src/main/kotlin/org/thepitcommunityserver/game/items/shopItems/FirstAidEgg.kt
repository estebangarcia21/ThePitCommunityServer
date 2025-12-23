package org.thepitcommunityserver.game.items.shopItems

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig
import org.thepitcommunityserver.util.Text


object FirstAidEgg : Item {
    override val config: ItemConfig
        get() = ItemConfig(
            name = "First-Aid Egg",
            material = Material.MONSTER_EGG,
            lore = listOf(
                "Heals <red>2.5${Text.HEART}</red>",
                "30 seconds cooldown",
                "-5 seconds on kill.",
                "<dark-gray>Lose speed on use</dark-gray>"
            ),
            itemColor = "red",
            unbreakable = true,
            data = 96,
        )

    @EventHandler
    fun onRightClick(event: PlayerInteractEvent) {

    }

    @EventHandler
    fun onPlayerKill(event: PlayerInteractEvent) {

    }
}