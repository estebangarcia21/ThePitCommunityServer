package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.Enchant
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.Timer
import org.thepitcommunityserver.util.intToRoman
import org.thepitcommunityserver.util.seconds
import org.thepitcommunityserver.util.undefPropErr

object Crush : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Crush",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.SWORD
        ) { "Strikes apply <red>Weakness ${intToRoman(amplifier[it])}</red><br/>(lasts, ${durationName[it]}, 2s cooldown)" }

    private val amplifier = mapOf(
        1 to 4,
        2 to 5,
        3 to 6
    )
    private val duration = mapOf(
        1 to 4,
        2 to 8,
        3 to 10
    )

    private val cooldown = 2L.seconds()

    private val durationName = duration.mapValues { it.value / 20f }

    private val timer = Timer()

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this) { damager, damaged, tier, _ ->
            val amplifier = amplifier[tier] ?: undefPropErr("amplifier", tier)
            val duration = duration[tier] ?: undefPropErr("duration", tier)

            timer.cooldown(damager.uniqueId, cooldown) {
                damaged.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, duration, amplifier, true))
            }
        }
    }
}
