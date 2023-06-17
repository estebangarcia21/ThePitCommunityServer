package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.util.*

object Healer : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Healer",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = true,
            type = EnchantType.SWORD,
        ) { "Hitting a player <green>heals</green> both you and them for <red>${hearts[it]}${Text.HEART}</red> (1s cooldown)" }

    private val healAmount = mapOf(
        1 to 2.0,
        2 to 4.0,
        3 to 6.0
    )

    private val hearts = healAmount.mapValues { it.value / 2f }

    private val timer = Timer()
    private val cooldownTime = Time(1L * SECONDS)

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this) {damager, damaged, tier, _, ->
            val healAmount = healAmount[tier] ?: undefPropErr("healAmount", tier)

            timer.cooldown(damager.uniqueId, cooldownTime.ticks()) {
                DamageManager.applyHeal(damager, healAmount)
                DamageManager.applyHeal(damaged, healAmount)
            }
        }
    }
}