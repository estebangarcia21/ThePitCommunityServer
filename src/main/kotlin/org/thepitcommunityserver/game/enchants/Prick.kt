package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.util.Text
import org.thepitcommunityserver.util.undefPropErr

object Prick : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Prick",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.PANTS
        ) { "Heal <red>${hearts[it]}${Text.HEART}</red> on arrow hit" }

    private val damageAmount = mapOf(
        1 to 0.5,
        2 to 0.75,
        3 to 1.0
    )

    private val hearts = damageAmount.mapValues { it.value / 2f }

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagedReceivedAnyHitWithPantsEnchant(this) { damager, damaged, tier, _ ->
            val damage = damageAmount[tier] ?: undefPropErr("damageAmount", tier)

            DamageManager.applyTrueDamage(damager, damaged, damage)
        }
    }
}
