package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.*
import org.thepitcommunityserver.util.Timer
import java.util.*

object SpeedyHit : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Crush",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = true,
            type = EnchantType.SWORD
        ) { "Gain Speed I for <${duration[it]?.seconds()}s</yellow> on hit(${cooldownTime[it]?.seconds()}s<br/>cooldown)" }

    private val duration = mapOf(
        1 to Time(5L * SECONDS),
        2 to Time(7L * SECONDS),
        3 to Time(9L * SECONDS)
    )

    private val timer = Timer<UUID>()

    private val cooldownTime  = mapOf(
        1 to Time(3L * SECONDS),
        2 to Time(2L * SECONDS),
        3 to Time(1L * SECONDS)
    )

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this) {
            val enchantTier = it.enchantTier

            val duration = duration[enchantTier] ?: undefPropErr("duration", enchantTier)
            val cooldownTime = cooldownTime[enchantTier] ?: undefPropErr("cooldownTime", enchantTier)

            timer.cooldown(it.damager.uniqueId, cooldownTime.ticks()) {
                it.damager.addPotionEffect(PotionEffect(PotionEffectType.SPEED, duration.ticks().toInt(), 0, true))
            }
        }
    }
}
