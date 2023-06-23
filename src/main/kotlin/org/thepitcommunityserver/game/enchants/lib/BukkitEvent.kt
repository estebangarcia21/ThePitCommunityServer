package org.thepitcommunityserver.game.enchants.lib

import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.thepitcommunityserver.game.events.ArrowWatch
import org.thepitcommunityserver.util.leggings
import kotlin.random.Random

typealias EntityDamageByEntityEventCallback<C> = (damager: Player, damaged: Player, tier: Int, ctx: C) -> Unit
typealias EntityShootBowEventCallback<C> = (shooter: Player, tier: Int, ctx: C) -> Unit
typealias ProjectileHitEventCallback<C> = (shooter: Player, tier: Int, ctx: C) -> Unit

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
            val tier = getEnchantTierForItem(enchant, ArrowWatch.getBowFromArrow(arrow))
            if (tier != null) {
                callback(shooter, damaged, tier, ArrowHitPlayerContext(arrow))
            }
        }
    }
}

fun EntityDamageByEntityEvent.arrowHitBlockingPlayer(enchant: Enchant, callback: EntityDamageByEntityEventCallback<ArrowHitPlayerContext>) {
    this.damagerArrowHitPlayerWithEnchant(enchant) { damager, damaged, tier, ctx ->
        if (damaged.isBlocking) {
            callback(damager, damaged, tier, ctx)
        }
    }
}

data class DamagedReceivedAnyHitWithPantsEnchantContext(val arrow: ArrowHitPlayerContext?) {
    val hitByMelee = arrow == null
    val hitByArrow = arrow != null
}

fun EntityDamageByEntityEvent.damagedReceivedAnyHitWithPantsEnchant(enchant: Enchant, callback: EntityDamageByEntityEventCallback<DamagedReceivedAnyHitWithPantsEnchantContext>) {
    val damaged = this.entity
    val damager = this.damager

    if (damaged !is Player) return

    val tier = getEnchantTierForItem(enchant, damaged.leggings) ?: return

    when (damager) {
        is Player -> {
            callback(damager, damaged, tier, DamagedReceivedAnyHitWithPantsEnchantContext(null))
        }
        is Arrow -> {
            val shooter = damager.shooter
            if (damager.shooter !is Player) return

            callback(shooter as Player, damaged, tier, DamagedReceivedAnyHitWithPantsEnchantContext(
                ArrowHitPlayerContext(damager)
            ))
        }
    }
}

data class ArrowShotWithEnchantContext(val arrow: Arrow)

fun EntityShootBowEvent.arrowShotWithEnchant(enchant: Enchant, callback: EntityShootBowEventCallback<ArrowShotWithEnchantContext>) {
    val shooter = this.entity
    val arrow = this.projectile

    if (shooter !is Player || arrow !is Arrow) return

    val bow = ArrowWatch.getBowFromArrow(arrow)
    val tier = getEnchantTierForItem(enchant, bow) ?: return

    callback(shooter, tier, ArrowShotWithEnchantContext(arrow))
}

fun chance(percent: Double): Boolean {
    require(percent in 0.0..1.0) { "Percent value must be between 0 and 1" }
    return Random.nextDouble() <= percent
}

fun playerDamagedPlayer(event: EntityDamageByEntityEvent): Boolean {
    if (event.entity !is Player) return false

    return event.damager is Player
}

fun EntityDamageByEntityEvent.playerHitPlayer(callback: (damager: Player, damaged: Player) -> Unit) {
    if (!playerDamagedPlayer(this)) return

    val damager = damager as? Player
    val damaged = entity as? Player

    if (damager != null && damaged != null) {
        callback(damager, damaged)
    }
}

fun ProjectileHitEvent.onArrowLand(enchant: Enchant, callback: ProjectileHitEventCallback<ArrowShotWithEnchantContext>) {
    val shooter = this.entity.shooter
    val arrow = this.entity

    if (shooter !is Player || arrow !is Arrow) return

    val bow = ArrowWatch.getBowFromArrow(arrow)
    val tier = getEnchantTierForItem(enchant, bow) ?: return

    callback(shooter, tier, ArrowShotWithEnchantContext(arrow))
}