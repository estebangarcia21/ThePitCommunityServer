package org.thepitcommunityserver.game.enchants.lib

import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
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
            ))
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

/**
 * Execute code if an arrow was shot with a bow that has an enchant.
 *
 * Explicitly set `getBowOnShoot` to `true` if you are calling this from an `EntityShootBowEvent`. `ProjectileHitEvent`s
 * should be set to false in order to use `ArrowWatch` to get the shooter's bow at the time of shooting the arrow upon
 * hitting an entity.
 */
fun EntityShootBowEvent.arrowShotWithEnchant(enchant: Enchant, getBowOnShoot: Boolean = false, callback: EventCallback<ArrowShotWithEnchantContext>) {
    val arrow = this.projectile as? Arrow ?: return
    val shooter = arrow.shooter as? Player ?: return

    val bow: ItemStack? = if (getBowOnShoot) {
        shooter.itemInHand
    } else {
        ArrowWatch.getBowFromArrow(arrow)
    }

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

data class PlayerHitPlayerContext(
    val damager: Player,
    val damaged: Player
)

fun EntityDamageByEntityEvent.playerHitPlayer(callback: EventCallback<PlayerHitPlayerContext> ) {
    val damager = this.damager
    val damaged = this.entity as? Player ?: return

    when (damager) {
        is Player -> {
            callback(PlayerHitPlayerContext(
                damager = damager,
                damaged = damaged
            ))
        }
        is Arrow -> {
            val shooter = damager.shooter as? Player ?: return

            callback(PlayerHitPlayerContext(
                damager = shooter,
                damaged = damaged
            ))
        }
    }
}

fun ProjectileHitEvent.onArrowLand(enchant: Enchant, callback: EventCallback<ArrowShotWithEnchantContext>) {
    val shooter = this.entity.shooter as? Player ?: return
    val arrow = this.entity as? Arrow ?: return

    val bow = ArrowWatch.getBowFromArrow(arrow)
    val enchantTier = getEnchantTierForItem(enchant, bow) ?: return

    callback(ArrowShotWithEnchantContext(
        shooter = shooter,
        enchantTier = enchantTier,
        arrow = arrow
    ))
}

data class DamagerMeleePlayerKillWithEnchantContext(
    val damager: Player,
    val damaged: Player,
    val enchantTier: Int
)

fun PlayerDeathEvent.damagerMeleeKillPlayerWithEnchant(enchant: Enchant, callback: EventCallback<DamagerMeleePlayerKillWithEnchantContext>) {
    val damaged = this.entity ?: return
    val damager = this.entity.killer ?: return

    val enchantTier = getEnchantTierForItem(enchant, damaged.itemInHand) ?: return

    callback(
        DamagerMeleePlayerKillWithEnchantContext(
            damager = damager,
            damaged = damaged,
            enchantTier = enchantTier
        )
    )
}

data class DamagerArrowPlayerKillWithEnchantContext(
    val shooter: Player,
    val damaged: Player,
    val enchantTier: Int
)

fun PlayerDeathEvent.damagerArrowKillPlayerWithEnchant(enchant: Enchant, callback: EventCallback<DamagerArrowPlayerKillWithEnchantContext>) {
    val shooter = this.entity.killer ?: return
    val damaged = this.entity ?: return

    val enchantTier = getEnchantTierForItem(enchant, shooter.itemInHand) ?: return

    callback(
        DamagerArrowPlayerKillWithEnchantContext(
            shooter = shooter,
            damaged = damaged,
            enchantTier = enchantTier
        )
    )
}

data class DamagerAnyKillPlayerWithPantsEnchantContext (
    val damager: Player,
    val damaged: Player,
    val enchantTier: Int,
)

fun PlayerDeathEvent.damagerAnyKillPlayerWithPantsEnchant(enchant: Enchant, callback: EventCallback<DamagerAnyKillPlayerWithPantsEnchantContext>) {
    val damaged = this.entity  ?: return
    val damager = this.entity.killer ?: return

    val enchantTier = getEnchantTierForItem(enchant, damager.leggings) ?: return

    callback(
        DamagerAnyKillPlayerWithPantsEnchantContext(
            damager = damager,
            damaged = damaged,
            enchantTier = enchantTier
        )
    )
}
