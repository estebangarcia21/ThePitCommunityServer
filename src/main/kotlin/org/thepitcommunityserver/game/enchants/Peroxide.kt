package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionEffectType.*
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.SECONDS
import org.thepitcommunityserver.util.Time
import org.thepitcommunityserver.util.intToRoman
import org.thepitcommunityserver.util.undefPropErr

object Peroxide : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Peroxide",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.PANTS
        ) { "Gain <red>Regen ${intToRoman(amplifier[it]?.inc())}</red> (${duration[it]?.seconds()}s) when hit" }

    private val amplifier = mapOf(
        1 to 0,
        2 to 0,
        3 to 1
    )

    private val duration = mapOf(
        1 to Time(5L * SECONDS),
        2 to Time(8L * SECONDS),
        3 to Time(8L * SECONDS)
    )

    @EventHandler
    fun onDamageEvent (event: EntityDamageByEntityEvent) {
        event.damagedReceivedAnyHitWithPantsEnchant(this){ _, damaged , tier, _ ->
            val amplifier = amplifier[tier] ?: undefPropErr("amplifier", tier)
            val duration = duration[tier] ?: undefPropErr("duration", tier)

            damaged.addPotionEffect(PotionEffect(REGENERATION, duration.ticks().toInt(), amplifier, true))
        }
    }
}
