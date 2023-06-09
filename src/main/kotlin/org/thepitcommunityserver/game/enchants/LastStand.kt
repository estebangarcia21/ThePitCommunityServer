package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.*

object LastStand : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Last Stand",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = false,
            type = EnchantType.PANTS
        ) { "Gain <blue>Resistance ${intToRoman(amplifier[it]?.inc())}</blue> (4 seconds)<br/>when reaching <red>3❤</red>" }

    private val amplifier = mapOf(
        1 to 0,
        2 to 1,
        3 to 2
    )

    @EventHandler
    fun onDamageEvent( event: EntityDamageByEntityEvent) {
        event.damagedReceivedAnyHitWithPantsEnchant(this){
            val damaged = it.damaged

            val amplifier = amplifier[it.enchantTier] ?: undefPropErr("amplifier", it.enchantTier)

            if (damaged.health < 10  ) {
                damaged.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Time(4L * SECONDS).ticks().toInt(), amplifier, true))
            }
        }
    }
}