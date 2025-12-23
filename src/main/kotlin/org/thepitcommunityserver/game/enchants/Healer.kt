package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.util.*
import org.thepitcommunityserver.util.Timer
import java.util.*

object Healer : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Healer",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = true,
            type = EnchantType.SWORD,
            description
        )

    private val description: EnchantDescription = {
        "Your hits <green>heal</green> you for <red>${damagedHearts[it]?.toInt()}${Text.HEART}</red><br/>and them for <red>${damagerHearts[it]?.toInt()}${Text.HEART}</red> ($cooldown}s cooldown)"
    }

    private val damagedHealAmount = mapOf(
        1 to 4.0,
        2 to 8.0,
        3 to 10.0
    )

    private val damagerHealAmount = mapOf(
        1 to 1.0,
        2 to 1.5,
        3 to 2.0
    )

    private val damagedHearts = damagedHealAmount.mapValues { it.value / 2f }
    private val damagerHearts = damagerHealAmount.mapValues { it.value / 2f }

    private val timer = Timer<UUID>()
    private val cooldown = Time(1L * SECONDS)

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this) {
            val damager = it.damager
            val damaged = it.damaged

            val damagedHealAmount = damagedHealAmount[it.enchantTier] ?: undefPropErr("healAmount", it.enchantTier)
            val damagerHealAmount = damagerHealAmount[it.enchantTier] ?: undefPropErr("healAmount", it.enchantTier)

            timer.cooldown(damager.uniqueId, cooldown.ticks()) {
                DamageManager.applyHeal(damager, damagerHealAmount)
                DamageManager.applyHeal(damaged, damagedHealAmount)
            }
        }
    }
}