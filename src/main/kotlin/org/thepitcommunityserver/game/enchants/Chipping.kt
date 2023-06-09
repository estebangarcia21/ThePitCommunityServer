package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.util.Text
import org.thepitcommunityserver.util.undefPropErr

object Chipping : Enchant{
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Chipping",
            tiers = listOf(1, 2, 3, 4, 5),
            group = EnchantGroup.B,
            rare = false,
            type = EnchantType.BOW,
            description
        )

    private val damageAmount = mapOf(
        1 to 1.0,
        2 to 2.0,
        3 to 3.0,
        4 to 3.5,
        5 to 4.0,
    )
    private val hearts = damageAmount.mapValues { it.value / 2f }

    private val description: EnchantDescription = {"Deals <red>${hearts[it]}${Text.HEART}</red> extra true damage"}

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerArrowHitPlayerWithEnchant(this) {
            val damage = damageAmount[it.enchantTier] ?: undefPropErr("damageAmount", it.enchantTier)

            DamageManager.applyTrueDamage(it.damaged, it.damager, damage)
        }
    }
}
