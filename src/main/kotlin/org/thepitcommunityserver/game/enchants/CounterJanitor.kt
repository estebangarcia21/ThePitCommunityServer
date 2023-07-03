package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.SECONDS
import org.thepitcommunityserver.util.Time
import org.thepitcommunityserver.util.undefPropErr

object CounterJanitor : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Counter-Janitor",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.SWORD
        ) { "Gain <yellow>Resistance I</yellow> (${duration[it]?.seconds()}s) on kill " }

    private val duration = mapOf(
        1 to Time(2L * SECONDS),
        2 to Time(3L * SECONDS),
        3 to Time(5L * SECONDS)
    )

    @EventHandler
    fun onKillEvent(event: PlayerDeathEvent) {
        event.damagerMeleeKillPlayerWithEnchant(this) {
            val damager = it.damager
            val duration = duration[it.enchantTier] ?: undefPropErr("duration", it.enchantTier)

            damager.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration.ticks().toInt(), 0, true))
        }
    }
}