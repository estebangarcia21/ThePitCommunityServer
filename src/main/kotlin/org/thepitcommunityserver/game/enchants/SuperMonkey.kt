package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.leggings

object SuperMonkey : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Super Monkey",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = true,
            type = EnchantType.PANTS
        ) { "Climb like <yellow>in4red</yellow>" }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        val leggings = player.leggings

        if (isNullItemStack(leggings)) return
        val enchantTier = getEnchantTierForItem(this, leggings) ?: return

        val blockInFront = player.location.add(player.eyeLocation.direction.normalize()).block
        if (player.isSneaking && blockInFront.type.isSolid) {
            // Climb like a monkey!!!
            player.velocity = Vector(0.0, 0.5, 0.0)
        }
    }
}
