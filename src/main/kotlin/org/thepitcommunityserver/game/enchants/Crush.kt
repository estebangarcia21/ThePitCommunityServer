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
        ) { "Strikes apply <red>Weakness ${intToRoman(amplifier[it]?.inc())}</red><br/>(lasts, ${seconds[it]}, 2s cooldown)" }

    private val amplifier = mapOf(
        1 to 4,
        2 to 5,
        3 to 6
    )

    private val seconds = mapOf(
        1 to 0.2f,
        2 to 0.4f,
        3 to 0.5
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
        event.damagerMeleeHitPlayerWithEnchant(this) {
            val enchantTier = it.enchantTier

            val amplifier = amplifier[enchantTier] ?: undefPropErr("amplifier", enchantTier)
            val duration = duration[enchantTier] ?: undefPropErr("duration", enchantTier)

            timer.cooldown(it.damager.uniqueId, cooldown.seconds()) {
                it.damaged.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, duration.ticks().toInt(), amplifier, true))
            }
        }
    }
}
