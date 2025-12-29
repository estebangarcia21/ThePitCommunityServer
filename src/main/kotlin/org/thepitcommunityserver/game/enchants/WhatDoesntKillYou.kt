package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.util.*
import org.thepitcommunityserver.util.Timer
import java.util.*

object WhatDoesntKillYou : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "What Doesn't Kill You",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = false,
            type = EnchantType.BOW,
            description
        )

    // TODO : Add description
    private val description: EnchantDescription =
        { "Heal ${hearts[it]}${Text.HEART} when shooting yourself. 3 second cooldown." }

    private val healAmount = mapOf(
        1 to 3.0,
        2 to 5.0,
        3 to 7.0
    )

    private val hearts = healAmount.mapValues { it.value / 2f }

    private val timer = Timer<UUID>()
    private val cooldown = Time(3L * SECONDS)

    @EventHandler
    fun onArrowHit(event: EntityDamageByEntityEvent) {
        event.damagerArrowHitPlayerWithEnchant(this) {
            val damager = it.damager
            val damaged = it.damaged
            val healAmount = healAmount[it.enchantTier] ?: undefPropErr("healAmount", it.enchantTier)

            timer.cooldown(damager.uniqueId, cooldown.ticks()) {
                if (damager == damaged) {
                    DamageManager.applyHeal(it.damager, healAmount)
                }
            }
        }
    }
}
