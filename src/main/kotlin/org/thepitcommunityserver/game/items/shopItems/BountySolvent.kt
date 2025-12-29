package org.thepitcommunityserver.game.items.shopItems

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemFlag
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig


object BountySolvent : Item {
    override val config: ItemConfig
        get() = ItemConfig(
            name = "Bounty Solvent (1:00)",
            material = Material.POTION,
            lore = listOf(
                "Receive <blue>-30% damage</blue> from players",
                "with a <gold>1000g</gold> bounty.",
                "",
                "Earn <gold>+50% gold</gold> from claimed bounties."
            ),
            data = 10,
            unbreakable = true,
            itemColor = "gold",
            flags = listOf(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES),
        )

    // TODO: Complete Damage Manager
    @EventHandler
    fun onPotionDrink(event: PlayerItemConsumeEvent) {
        event.isCancelled = true

        event.player.itemInHand = null
    }
}