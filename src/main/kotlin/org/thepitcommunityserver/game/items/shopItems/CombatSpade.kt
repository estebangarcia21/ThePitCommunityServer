package org.thepitcommunityserver.game.items.shopItems

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemFlag
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig
import org.thepitcommunityserver.util.NBT


object CombatSpade : Item {
    override val config: ItemConfig
        get() = ItemConfig(
            name = "Combat Spade",
            material = Material.DIAMOND_SPADE,
            itemColor = "aqua",
            lore = listOf("Deals <blue>+1 damage</blue> per", "<aqua>diamond piece</aqua> on enemy."),
            unbreakable = true,
            flags = listOf(ItemFlag.HIDE_UNBREAKABLE),
            nbtTags = mapOf(NBT.LOSE_ON_DEATH.entry)
        )


    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {

    }
}