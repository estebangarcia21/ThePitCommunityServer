package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.SECONDS
import org.thepitcommunityserver.util.Time
import org.thepitcommunityserver.util.leggings

object SuperMonkey : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Super Monkey",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = true,
            type = EnchantType.PANTS
        ) { "Climb like <yellow>in4red</yellow>. Gives <green>Jump Boost II</green> and <yellow>Speed II</yellow> 30s." }


    private val duration = Time(30L * SECONDS)
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        val leggings = player.leggings

        if (isEmptyItemStack(leggings)) return
        val enchantTier = getEnchantTierForItem(this, leggings) ?: return

        val blockInFront = player.location.add(player.eyeLocation.direction.normalize()).block
        if (player.isSneaking && blockInFront.type.isSolid) {
            // Climb like a monkey!!!
            player.velocity = Vector(0.0, 0.5, 0.0)
        }

        player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, duration.ticks().toInt(), 1, true))
        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, duration.ticks().toInt(), 1, true))
    }
}
