package org.thepitcommunityserver.game.events

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.Mirror
import org.thepitcommunityserver.game.enchants.lib.getEnchantTierForItem
import org.thepitcommunityserver.util.GlobalTimer
import org.thepitcommunityserver.util.TICK
import org.thepitcommunityserver.util.leggings

object DamageManager : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDamageEvent(event: EntityDamageByEntityEvent) {}

    fun applyTrueDamage(target: Player, damager: Player, raw: Double) {
        fun subtractHealth(player: Player, amount: Double) {
            val targetHealth = player.health - amount
            if (targetHealth <= 0.0) {
                player.health = 0.0
            } else {
                player.health = targetHealth
            }
        }

        val leggings = target.leggings
        val mirrorTier = getEnchantTierForItem(Mirror, leggings)

        GlobalTimer.after(1 * TICK) {
            if (mirrorTier == null) {
                subtractHealth(target, raw)
                return@after
            }

            val reflectionAmount = Mirror.reflectionAmounts[mirrorTier]
            reflectionAmount?.let { subtractHealth(damager, raw * it) }
        }
    }

    fun applyHeal(target: Player, amount: Double) {
        target.health = (target.health + amount).coerceAtMost(target.maxHealth)
    }
}
