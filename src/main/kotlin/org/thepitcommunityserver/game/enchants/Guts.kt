package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.util.Text
import org.thepitcommunityserver.util.intToRoman
import org.thepitcommunityserver.util.undefPropErr

object Guts : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Guts",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = false,
            type = EnchantType.SWORD
        ) { "Heal <red>${hearts[it]} ${Text.HEART}</red> on kill" }

    private val healAmount = mapOf(
        1 to 1.0,
        2 to 1.5,
        3 to 2.0,
    )

    private val hearts = healAmount.mapValues { it.value / 2f }

    @EventHandler
    fun onKillEvent(event: PlayerDeathEvent) {
        event.damagerMeleeKillPlayerWithEnchant(this) {
            val damager = it.damager
            val healAmount = healAmount[it.enchantTier] ?: undefPropErr("healAmount", it.enchantTier)

            DamageManager.applyHeal(damager,healAmount)
        }
    }
}