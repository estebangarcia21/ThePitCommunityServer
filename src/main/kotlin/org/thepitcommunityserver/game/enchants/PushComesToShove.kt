package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.*
import java.util.*

object PushComesToShove : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Push Comes to Shove",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = false,
            type = EnchantType.BOW,
            description
        )


    private val description: EnchantDescription = {
        val punch = mapOf(
            1 to "III",
            2 to "V",
            3 to "VII"
        )

        if (it == 1) {
            "Every third arrow hit has the vanilla Punch ${punch[it]}"
        } else {
            "Every third arrow hit deals <red>+${hearts[it]}${Text.HEART}</red> has the vanilla Punch ${punch[it]}"
        }
    }

    private val amplifier = mapOf(
        1 to 12f,
        2 to 25f,
        3 to 35f
    )

    private val damageAmount = mapOf(
        1 to 0.0,
        2 to 1.0,
        3 to 2.0
    )

    private val hearts = damageAmount.mapValues { it.value / 2f }

    private val hitCounter = HitCounter<UUID>()

    @EventHandler
    fun onArrowHit(event: EntityDamageByEntityEvent) {
        event.damagerArrowHitPlayerWithEnchant(this) {
            val damager = it.damager
            val damaged = it.damaged
            val arrow = it.arrow

            val amplifier = amplifier[it.enchantTier] ?: undefPropErr("amplifier", it.enchantTier)
            val damageAmount = damageAmount[it.enchantTier] ?: undefPropErr("damageAmount", it.enchantTier)

            hitCounter.onNthHit(damager.uniqueId, 4, cooldown = Time(5 * SECONDS)) {

                val velocity = arrow.velocity.normalize().multiply(amplifier / 2.35)
                velocity.setY(0)
                damaged.velocity = velocity
                damaged.damage(damageAmount)
            }
        }
    }
}
