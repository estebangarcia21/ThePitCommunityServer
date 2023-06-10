package org.thepitcommunityserver.game.enchants.lib

import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import kotlin.random.Random

typealias EntityDamageByEntityEventCallback<C> = (damager: Player, damaged: Player, tier: Int, ctx: C) -> Unit

fun EntityDamageByEntityEvent.damagerMeleeHitPlayerWithEnchant(enchant: Enchant, callback: EntityDamageByEntityEventCallback<Unit>) {
    val damaged = this.entity
    val damager = this.damager

    if (damager is Player && damaged is Player) {
        val tier = getEnchantTierForItem(enchant, damager.itemInHand)
        if (tier != null) {
            callback(damager, damaged, tier, Unit)
        }
    }
}

data class ArrowHitPlayerContext(val arrow: Arrow)

fun EntityDamageByEntityEvent.damagerArrowHitPlayerWithEnchant(enchant: Enchant, callback: EntityDamageByEntityEventCallback<ArrowHitPlayerContext>) {
    val damaged = this.entity

    if (this.damager is Arrow && damaged is Player) {
        val arrow = this.damager as Arrow
        val shooter = arrow.shooter

        if (shooter is Player) {
            val tier = getEnchantTierForItem(enchant, shooter.itemInHand)
            if (tier != null) {
                callback(shooter, damaged, tier, ArrowHitPlayerContext(arrow))
            }
        }
    }
}

fun EntityDamageByEntityEvent.arrowHitBlockingPlayer(enchant: Enchant, callback: EntityDamageByEntityEventCallback<ArrowHitPlayerContext>) {
    this.damagerArrowHitPlayerWithEnchant(enchant) { damager, damaged, tier, ctx ->
        run {
            if (damaged.isBlocking) {
                callback(damager, damaged, tier, ctx)
            }
        }
    }
}

fun chance(percent: Double): Boolean {
    require(percent in 0.0..1.0) { "Percent value must be between 0 and 1" }
    return Random.nextDouble() <= percent
}
