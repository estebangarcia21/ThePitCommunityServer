package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.SECONDS
import org.thepitcommunityserver.util.Time
import org.thepitcommunityserver.util.intToRoman
import org.thepitcommunityserver.util.undefPropErr

object SprintDrain: Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Sprint Drain",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.BOW,
            description
        )

    private val amplifier = mapOf(
        1 to 0,
        2 to 0,
        3 to 1
    )

    private val duration = mapOf(
        1 to Time(5L * SECONDS),
        2 to Time(5L * SECONDS),
        3 to Time(7L * SECONDS)
    )

    private val description: EnchantDescription = {
        if (it == 1) {
            "Arrow shots grant you <yellow>Speed ${intToRoman(amplifier[it]?.inc())}</yellow><br/>({1}s)"
        } else {
            "Arrow shots grant you <yellow>Speed ${intToRoman(amplifier[it]?.inc())}</yellow><br/>(${duration[it]}s) and apply <blue>Slowness I</blue><br/>(3s)"
        }
    }

    @EventHandler
    fun onDamageEvent(event:EntityDamageByEntityEvent) {
        event.damagerArrowHitPlayerWithEnchant(this){damager, damaged, tier, _ ->
            val amplifier =  amplifier[tier] ?: undefPropErr("amplifier", tier)
            val duration =  duration[tier] ?: undefPropErr("duration", tier)

            if (tier == 1) {
                damager.addPotionEffect(PotionEffect(PotionEffectType.SPEED, amplifier, duration.ticks().toInt(),true))
            } else {
                damager.addPotionEffect(PotionEffect(PotionEffectType.SPEED, amplifier, duration.ticks().toInt(),true))
                damaged.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 0, 60,true))
            }
        }
    }
}