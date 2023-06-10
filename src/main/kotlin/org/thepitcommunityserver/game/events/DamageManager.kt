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
    @EventHandler(priority = EventPriority.LOWEST)
    fun onDamageEvent(event: EntityDamageByEntityEvent) {}

    fun applyDamage(damagee: Player, raw: Double, damager: Player? = null) {}

    fun applyTrueDamage(damagee: Player, damager: Player, raw: Double) {
        fun subtractHealth(player: Player, amount: Double) {
            player.health = (player.health - amount).coerceAtLeast(0.0)
        }

        val leggings = damagee.leggings
        val mirrorTier = getEnchantTierForItem(Mirror, leggings)

        if (mirrorTier == null) {
            subtractHealth(damagee, raw)
            return
        }

        val reflectionAmount = Mirror.reflectionAmounts[mirrorTier]
        reflectionAmount?.let { subtractHealth(damager, raw * it) }
    }

    fun applyHeal(damagee: Player, damager: Player, raw:Double) {
        fun addHealth(player: Player, amount: Double) {
            player.health = (player.health + amount).coerceAtMost(player.maxHealth)
        }
    }
}
