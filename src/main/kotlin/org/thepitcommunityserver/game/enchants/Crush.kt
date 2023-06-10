package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.Enchant
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.Text
import org.thepitcommunityserver.util.intToRoman
import org.thepitcommunityserver.util.undefPropErr

object Crush : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Crush",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.SWORD,
            description
        )

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
    private val durationName = duration.mapValues { it.value / 10f }

    private val description: EnchantDescription = { "Strikes apply <red>Weakness ${intToRoman(it)}</red><br/>(lasts, ${durationName[it]}, 2s cooldown)" }

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this) { damager, damaged, tier, _ ->
            val amp = amplifier[tier] ?: undefPropErr("amplifier", tier)
            val duration = duration[tier] ?: undefPropErr("duration", tier)

            damaged.player.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, duration, amp, true))
            //TODO Create Global timer for the cooldown ... can work on later on
        }
    }
}
