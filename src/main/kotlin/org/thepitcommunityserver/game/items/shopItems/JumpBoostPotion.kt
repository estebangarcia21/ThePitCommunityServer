package org.thepitcommunityserver.game.items.shopItems

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig
import org.thepitcommunityserver.util.SECONDS
import org.thepitcommunityserver.util.Time


object JumpBoostPotion : Item {
    override val config: ItemConfig
        get() = ItemConfig(
            name = "Potion of Jump IV",
            itemColor = "white",
            material = Material.POTION,
            data = 10,
            unbreakable = true,
            lore = listOf(
                "Jump Boost IV (0:30)",
            ),
            flags = listOf(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES),
        )

    @EventHandler
    fun onPotionDrink(event: PlayerItemConsumeEvent) {
        val player = event.player
        
        player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, Time(30L * SECONDS).ticks().toInt(), 3))


    }
}