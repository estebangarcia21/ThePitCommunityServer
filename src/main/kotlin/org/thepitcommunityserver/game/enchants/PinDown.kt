package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.SECONDS
import org.thepitcommunityserver.util.Time
import org.thepitcommunityserver.util.Timer
import org.thepitcommunityserver.util.damagerArrowHitPlayerWithEnchant
import java.util.*

object PinDown : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Pin down",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = false,
            type = EnchantType.BOW,
            description
        )

    private val description: EnchantDescription =
        { "Fully charged shots pin the victim<br/>down, preventing them from gaining<br/>Speed or Jump Boost (${seconds[it]?.seconds()}s)" }

    private val timer = Timer<UUID>()
    private val seconds = mapOf(
        1 to Time(3L * SECONDS),
        2 to Time(5L * SECONDS),
        3 to Time(10L * SECONDS),
    )

    @EventHandler
    private fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerArrowHitPlayerWithEnchant(this) {
            val damaged = it.damaged

            damaged.removePotionEffect(PotionEffectType.SPEED)
            damaged.removePotionEffect(PotionEffectType.JUMP)

            timer.after(
                damaged.uniqueId, seconds[it.enchantTier]!!.ticks(), {
                    damaged.removePotionEffect(PotionEffectType.SPEED)
                    damaged.removePotionEffect(PotionEffectType.JUMP)
                }
            ) {

            }
        }
    }
}