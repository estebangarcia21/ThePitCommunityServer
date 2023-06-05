package org.thepitcommunityserver.game.enchants

import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.util.Text

object Gamble : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Gamble",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = true,
            type = EnchantType.SWORD,
            description
        )

    private val description: EnchantDescription = { "<light-purple>50% chance</light-purple> to deal <red>${hearts[it]}${Text.HEART}</red> true<br/>damage to whoever you hit, or to<br/>yourself" }

    private const val PROC_CHANCE = 0.5

    private val damageAmount = mapOf(
        1 to 2.0,
        2 to 4.0,
        3 to 6.0
    )
    private val hearts = damageAmount.mapValues { it.value / 2f }

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this) { damager, damagee, tier ->
            val damage = damageAmount[tier]

            if (chance(PROC_CHANCE)) {
                damage?.let { DamageManager.applyTrueDamage(damagee, damager, it) }

                damager.playSound(damager.location, Sound.NOTE_PLING, 1f, 3f)
            } else {
                damage?.let { DamageManager.applyTrueDamage(damager, damager, it) }

                damager.playSound(damager.location, Sound.NOTE_PLING, 1f, 3f)
                damagee.playSound(damager.location, Sound.NOTE_PLING, 1f, 1.5f)
            }
        }
    }
}
