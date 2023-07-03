package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.SECONDS
import org.thepitcommunityserver.util.Time
import org.thepitcommunityserver.util.undefPropErr

object SpeedyKill : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Counter-Janitor",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.SWORD
        ) { "Gain <yellow>Speed I</yellow> (${duration[it]?.seconds()}s) on kill " }

    private val duration = mapOf(
        1 to Time(4L * SECONDS),
        2 to Time(7L * SECONDS),
        3 to Time(12L * SECONDS)
    )

    @EventHandler
    fun onKillEvent(event: PlayerDeathEvent) {
        event.damagerMeleeKillPlayerWithEnchant(this) {
            val damager = it.damager
            val duration = duration[it.enchantTier] ?: undefPropErr("duration", it.enchantTier)

            damager.addPotionEffect(PotionEffect(PotionEffectType.SPEED, duration.ticks().toInt(), 0, true))
        }
    }
}
