package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.Enchant
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.*
import org.thepitcommunityserver.util.Timer
import java.util.*

object Crush : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Crush",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.SWORD
        ) { "Strikes apply <red>Weakness ${intToRoman(amplifier[it])}</red><br/>(lasts, ${duration[it]?.seconds()}, 2s cooldown)" }

    private val amplifier = mapOf(
        1 to 4,
        2 to 5,
        3 to 6
    )
    private val duration = mapOf(
        1 to Time(4L * TICK),
        2 to Time(8L * TICK),
        3 to Time(10L * TICK)
    )

    private val cooldown = Time(2L * SECONDS)

    private val timer = Timer<UUID>()

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this) { damager, damaged, tier, _ ->
            val amplifier = amplifier[tier] ?: undefPropErr("amplifier", tier)
            val duration = duration[tier] ?: undefPropErr("duration", tier)

            timer.cooldown(damager.uniqueId, cooldown.ticks()) {
                damaged.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, duration.ticks().toInt(), amplifier, true))
            }
        }
    }
}
