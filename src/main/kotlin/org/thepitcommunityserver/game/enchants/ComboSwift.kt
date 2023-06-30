package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.*
import java.util.*

object ComboSwift: Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Combo: Swift",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = true,
            type = EnchantType.SWORD,
        ) { "Every <yellow>${word[it]}</yellow> strike gain<br/><yellow>Speed ${intToRoman(amplifier[it]?.inc())}</yellow> (${duration[it]?.seconds()})" }

    private val word = mapOf(
        1 to "fourth",
        2 to "third",
        3 to "third"
    )

    private val duration = mapOf(
        1 to Time(3L * SECONDS),
        2 to Time(4L * SECONDS),
        3 to Time(5L * SECONDS)
    )

    private val amplifier = mapOf(
        1 to 0,
        2 to 1,
        3 to 1
    )

    private val hitsNeeded = mapOf(
        1 to 4,
        2 to 3,
        3 to 3
    )

    private val hitCounter = HitCounter<UUID>()

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this) {
            val damager = it.damager

            val amplifier = amplifier[it.enchantTier] ?: undefPropErr("amplifier", it.enchantTier)
            val duration = duration[it.enchantTier] ?: undefPropErr("duration", it.enchantTier)
            val hitsNeeded = hitsNeeded[it.enchantTier] ?: undefPropErr("hitsNeeded", it.enchantTier)

            hitCounter.onNthHit(damager.uniqueId, hitsNeeded) {
                damager.addPotionEffect(PotionEffect(PotionEffectType.SPEED, duration.ticks().toInt(), amplifier), true
                )
            }
        }
    }
}
