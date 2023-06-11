package org.thepitcommunityserver.game.events

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.Mirror
import org.thepitcommunityserver.game.enchants.lib.getEnchantTierForItem
import org.thepitcommunityserver.util.leggings

object DamageManager : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDamageEvent(event: EntityDamageByEntityEvent) {}

    fun applyTrueDamage(target: Player, damager: Player, raw: Double) {
        fun subtractHealth(player: Player, amount: Double) {
            player.health = (player.health - amount).coerceAtLeast(0.0)
        }

        val leggings = target.leggings
        val mirrorTier = getEnchantTierForItem(Mirror, leggings)

        if (mirrorTier == null) {
            subtractHealth(target, raw)
            return
        }

        val reflectionAmount = Mirror.reflectionAmounts[mirrorTier]
        reflectionAmount?.let { subtractHealth(damager, raw * it) }
    }

    fun applyHeal(target: Player, amount: Double) {
        target.health = (target.health + amount).coerceAtMost(target.maxHealth)
    }
}
