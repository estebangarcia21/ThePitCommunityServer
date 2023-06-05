package org.thepitcommunityserver.game.enchants.lib

import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import kotlin.random.Random

typealias EntityDamageByEntityEventCallback = (damager: Player, damagee: Player, tier: Int) -> Unit

fun EntityDamageByEntityEvent.damagerMeleeHitPlayerWithEnchant(enchant: Enchant, callback: EntityDamageByEntityEventCallback) {
    val damagee = this.entity
    val damager = this.damager

    if (damager is Player && damagee is Player) {
        val tier = getEnchantTierForItem(enchant, damager.itemInHand)
        if (tier != NON_EXISTENT) {
            callback(damager, damagee, tier)
        }
    }
}

fun EntityDamageByEntityEvent.damagerArrowHitPlayerWithEnchant(enchant: Enchant, callback: EntityDamageByEntityEventCallback) {
    val damagee = this.entity

    if (this.damager is Arrow && damagee is Player) {
        val arrow = this.damager as Arrow
        val shooter = arrow.shooter

        if (shooter is Player) {
            val tier = getEnchantTierForItem(enchant, shooter.itemInHand)
            if (tier != NON_EXISTENT) {
                callback(shooter, damagee, tier)
            }
        }
    }
}

fun chance(percent: Double): Boolean {
    require(percent in 0.0..1.0) { "Percent value must be between 0 and 1" }
    return Random.nextDouble() <= percent
}
