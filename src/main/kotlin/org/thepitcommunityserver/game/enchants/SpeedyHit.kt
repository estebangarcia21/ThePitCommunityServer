package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.*

object SpeedyHit : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Crush",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.SWORD
        ) { "Gain Speed I for <${duration[it]?.seconds() }s</yellow> on hit(${cooldownTime[it]?.seconds()}s<br/>cooldown)" }

    private val timer = Timer()

    private val duration = mapOf(
        1 to Time(5L * SECONDS),
        2 to Time(7L * SECONDS),
        3 to Time(9L * SECONDS)
    )

    private val cooldownTime  = mapOf(
        1 to Time(3L * SECONDS),
        2 to Time(2L * SECONDS),
        3 to Time(1L * SECONDS)
    )
    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this){damager, _, tier, _ ->
            val duration = duration[tier] ?: undefPropErr("duration", tier)
            val cooldownTime = cooldownTime[tier] ?: undefPropErr("cooldownTime", tier)

            timer.cooldown(damager.uniqueId, cooldownTime.ticks()) {
                damager.addPotionEffect(PotionEffect(PotionEffectType.SPEED, duration.ticks().toInt(), 0, true))
            }
        }
    }
}
