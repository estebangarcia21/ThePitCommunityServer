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
        ) { "Enemies hitting you receive <red>${hearts[it]}${Text.HEART}</red><br/>true damage" }

    private val description: EnchantDescription = {
        if (it == 3) {
            "Enemies hitting you receive <red>${hearts[it]}${Text.HEART}</red><br/>true damage"
        } else {
            "Enemies hitting you receive<br/><red>${hearts[it]}${Text.HEART}</red> true damage"
        }
    }

    private val damageAmount = mapOf(
        1 to 0.5,
        2 to 0.75,
        3 to 1.0
    )

    private val hearts = damageAmount.mapValues { it.value / 2f }

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagedReceivedAnyHitWithPantsEnchant(this) {
            val damage = damageAmount[it.enchantTier] ?: undefPropErr("damageAmount", it.enchantTier)

            DamageManager.applyTrueDamage(it.damager, it.damaged, damage)
        }
    }
}
