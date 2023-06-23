package org.thepitcommunityserver.game.enchants.lib

import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.thepitcommunityserver.game.events.ArrowWatch
import org.thepitcommunityserver.util.leggings
import kotlin.random.Random


typealias EventCallback<C> = (ctx: C) -> Unit

data class PlayerMeleeHitPlayerContext(
    val damager: Player,
    val damaged: Player,
    val enchantTier: Int
)

fun EntityDamageByEntityEvent.damagerMeleeHitPlayerWithEnchant(enchant: Enchant, callback: EventCallback<PlayerMeleeHitPlayerContext>) {
    val damaged = this.entity as? Player ?: return
    val damager = this.damager as? Player ?: return
    val enchantTier = getEnchantTierForItem(enchant, damager.itemInHand) ?: return

    callback(PlayerMeleeHitPlayerContext(
        damager = damager,
        damaged = damaged,
        enchantTier = enchantTier
    ))
}

data class ArrowHitPlayerContext(
    val damager: Player,
    val damaged: Player,
    val enchantTier: Int,
    val arrow: Arrow
)

fun EntityDamageByEntityEvent.damagerArrowHitPlayerWithEnchant(enchant: Enchant, callback: EventCallback<ArrowHitPlayerContext>) {
    val damaged = this.entity as? Player ?: return
    val arrow = this.damager as? Arrow ?: return
    val shooter = arrow.shooter as? Player ?: return
    val enchantTier = getEnchantTierForItem(enchant, ArrowWatch.getBowFromArrow(arrow)) ?: return

    callback(ArrowHitPlayerContext(
        damager = shooter,
        damaged = damaged,
        enchantTier = enchantTier,
        arrow = arrow
    ))
}

fun EntityDamageByEntityEvent.arrowHitBlockingPlayer(enchant: Enchant, callback: EventCallback<ArrowHitPlayerContext>) {
    this.damagerArrowHitPlayerWithEnchant(enchant) { ctx ->
        if (ctx.damaged.isBlocking) {
            callback(ctx)
        }
    }
}

data class DamagedReceivedAnyHitWithPantsEnchantContext(
    val damager: Player,
    val damaged: Player,
    val enchantTier: Int,
    val arrow: Arrow?
) {
    val hitByMelee = arrow == null
    val hitByArrow = arrow != null
}

fun EntityDamageByEntityEvent.damagedReceivedAnyHitWithPantsEnchant(enchant: Enchant, callback: EventCallback<DamagedReceivedAnyHitWithPantsEnchantContext>) {
    val damaged = this.entity as? Player ?: return
    val damager = this.damager

    val enchantTier = getEnchantTierForItem(enchant, damaged.leggings) ?: return

    when (damager) {
        is Player -> {
            callback(
                DamagedReceivedAnyHitWithPantsEnchantContext(
                damager = damager,
                damaged = damaged,
                enchantTier = enchantTier,
                arrow = null
            )
            )
        }
        is Arrow -> {
            val shooter = damager.shooter as? Player ?: return

            callback(DamagedReceivedAnyHitWithPantsEnchantContext(
                damager = shooter,
                damaged = damaged,
                enchantTier = enchantTier,
                arrow = damager
            ))
        }
    }
}

data class ArrowShotWithEnchantContext(
    val shooter: Player,
    val enchantTier: Int,
    val arrow: Arrow
)

fun EntityShootBowEvent.arrowShotWithEnchant(enchant: Enchant, callback: EventCallback<ArrowShotWithEnchantContext>) {
    val shooter = this.entity as? Player ?: return
    val arrow = this.projectile as? Arrow ?: return

    val bow = ArrowWatch.getBowFromArrow(arrow)
    val enchantTier = getEnchantTierForItem(enchant, bow) ?: return

    callback(ArrowShotWithEnchantContext(
        shooter = shooter,
        enchantTier = enchantTier,
        arrow = arrow
    ))
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

fun ProjectileHitEvent.onArrowLand(enchant: Enchant, callback: EventCallback<ArrowShotWithEnchantContext>) {
    val shooter = this.entity.shooter
    val arrow = this.entity

    if (shooter !is Player || arrow !is Arrow) return

    val bow = ArrowWatch.getBowFromArrow(arrow)
    val enchantTier = getEnchantTierForItem(enchant, bow) ?: return

    callback(ArrowShotWithEnchantContext(
        shooter = shooter,
        enchantTier = enchantTier,
        arrow = arrow
    ))
}