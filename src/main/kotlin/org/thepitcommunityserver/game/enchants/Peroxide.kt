package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionEffectType.*
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.intToRoman
import org.thepitcommunityserver.util.undefPropErr

object Peroxide : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Peroxide",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.PANTS,
            { "Gain <red>Regen ${intToRoman(amplifier[it])}</red> (${duration[it]}s) when hit" }
        )

    private val amplifier = mapOf(
        1 to 0,
        2 to 0,
        3 to 1

    )

    private val duration = mapOf(
        1 to 5,
        2 to 8,
        3 to 8,
    )

    @EventHandler
    fun onDamageEvent (event: EntityDamageByEntityEvent) {
        event.damagedReceivedAnyHitWithPantsEnchant(this){ _, damaged , tier, _ ->
            val amplifier = amplifier[tier] ?: undefPropErr("amplifier", tier)
            val duration = duration[tier] ?: undefPropErr("duration", tier)

            damaged.addPotionEffect(PotionEffect( PotionEffectType.REGENERATION, duration, amplifier, true))
        }
    }
}

