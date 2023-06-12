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

object Wasp : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Wasp",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.BOW
        ) { "Apply <red>Weakness ${intToRoman(amplifier[it]?.inc())}</red> (${duration[it]?.seconds()}s) on hit" }

    private val amplifier = mapOf(
        1 to 1,
        2 to 2,
        3 to 3
    )
    private val duration = mapOf(
        1 to Time(6L * SECONDS),
        2 to Time(11L * SECONDS),
        3 to Time(16L * SECONDS)
    )

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerArrowHitPlayerWithEnchant(this) { _, damaged, tier, _ ->
            val amplifier = amplifier[tier] ?: undefPropErr("amplifier", tier)
            val duration = duration[tier] ?: undefPropErr("duration", tier)

            damaged.player.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, duration.ticks().toInt(), amplifier * 20, true))
        }
    }
}
