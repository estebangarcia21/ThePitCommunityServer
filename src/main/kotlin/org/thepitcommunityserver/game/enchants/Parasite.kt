package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.util.Text
import org.thepitcommunityserver.util.undefPropErr

object Parasite : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Parasite",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.BOW
        ) { "Heal <red>${hearts[it]}${Text.HEART}</red> on arrow hit"}

    private val healAmount = mapOf(
        1 to 0.5,
        2 to 1.0,
        3 to 2.0
    )

    private val hearts = healAmount.mapValues { it.value / 2f }

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerArrowHitPlayerWithEnchant(this) {
            val heal = healAmount[it.enchantTier] ?: undefPropErr("healAmount", it.enchantTier)

            DamageManager.applyHeal(it.damager, heal)
        }
    }
}
