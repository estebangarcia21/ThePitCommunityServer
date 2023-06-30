package org.thepitcommunityserver.game.enchants

import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.util.HitCounter
import org.thepitcommunityserver.util.undefPropErr
import java.util.*

object ComboHeal : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Combo: Heal",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = false,
            type = EnchantType.SWORD,
        ) { "Every <yellow>fourth</yellow> strike heals<br/><red>${hearts[it]}❤</red> and grants <gold>${hearts[it]}❤</gold><br/>absorption" }

    private val healAmount = mapOf(
        1 to .8,
        2 to 1.6,
        3 to 2.4
    )

    private val hearts = healAmount.mapValues { it.value / 2 }
    private val hitCounter = HitCounter<UUID>()

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this) {
            val damager = it.damager
            val healAmount = healAmount[it.enchantTier] ?: undefPropErr("healAmount", it.enchantTier)

            hitCounter.onNthHit(damager.uniqueId, 4) {
                val nmsDamager = (damager as CraftPlayer).handle

                damager.playSound(damager.location, Sound.DONKEY_HIT, 1f, 0.5f)
                DamageManager.applyHeal(damager, healAmount)
                nmsDamager.absorptionHearts = (nmsDamager.absorptionHearts + healAmount.coerceAtMost(8.0)).toFloat()
            }
        }
    }
}
