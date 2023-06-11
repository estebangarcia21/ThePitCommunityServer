package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.intToRoman
import org.thepitcommunityserver.util.seconds
import org.thepitcommunityserver.util.undefPropErr

object LastStand : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Mirror",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.PANTS,
            { "Gain <blue>Resistance ${intToRoman(amplifier[it])}</blue> (4<br/>seconds) when reaching <red>3❤</red>" }
        )

    private val amplifier  = mapOf(
        1 to 0,
        2 to 1,
        3 to 2
    )

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagedReceivedAnyHitWithPantsEnchant(this) {_, damaged, tier, _ ->
            val amplifier = amplifier[tier] ?: undefPropErr("amplifier", tier)

            if (damaged.health <= 10) {
                damaged.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 4L.seconds().toInt(), amplifier, true ))
            }
        }
    }
}