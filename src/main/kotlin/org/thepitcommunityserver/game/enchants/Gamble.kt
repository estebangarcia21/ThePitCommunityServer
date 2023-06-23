package org.thepitcommunityserver.game.enchants

import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.util.Text
import org.thepitcommunityserver.util.undefPropErr

object Gamble : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Gamble",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = true,
            type = EnchantType.SWORD,
        ) { "<light-purple>50% chance</light-purple> to deal <red>${hearts[it]}${Text.HEART}</red> true<br/>damage to whoever you hit, or to<br/>yourself" }

    private const val PROC_CHANCE = 0.5

    private val damageAmount = mapOf(
        1 to 2.0,
        2 to 4.0,
        3 to 6.0
    )
    private val hearts = damageAmount.mapValues { it.value / 2f }

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this) {
            val damaged = it.damaged
            val damager = it.damager
            
            val damage = damageAmount[it.enchantTier] ?: undefPropErr("damageAmount", it.enchantTier)

            if (chance(PROC_CHANCE)) {
                DamageManager.applyTrueDamage(damaged, damager, damage)

                damager.playSound(damager.location, Sound.NOTE_PLING, 1f, 3f)
            } else {
                DamageManager.applyTrueDamage(damager, damager, damage)

                damager.playSound(damager.location, Sound.NOTE_PLING, 1f, 3f)
                damaged.playSound(damager.location, Sound.NOTE_PLING, 1f, 1.5f)
            }
        }
    }
}
