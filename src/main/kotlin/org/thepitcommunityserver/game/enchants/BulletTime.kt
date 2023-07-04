package org.thepitcommunityserver.game.enchants

import org.bukkit.Effect
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.undefPropErr

object BulletTime : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Bullet Time",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = false,
            type = EnchantType.SWORD,
            description
        )
    private val healAmount = mapOf(
        1 to 0.0,
        2 to 2.0,
        3 to 3.0
    )
    private val hearts = healAmount.mapValues { it.value / 2f }

    private val description: EnchantDescription = {
        if (it == 1) {
            "Blocking destroys arrows that hit<br/>you"
        } else {
            "Blocking destroys arrows that hit<br/>you. Destroying arrows this way<br/>heals <red>${hearts[it]}❤</red>"
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.arrowHitBlockingPlayer(this) {
            val arrow = it.arrow
            val damaged = it.damaged
            val healAmount = healAmount[it.enchantTier] ?: undefPropErr("healAmount", it.enchantTier)

            event.isCancelled = true

            arrow.knockbackStrength = 0
            arrow.setBounce(true)

            damaged.world.playSound(damaged.location, Sound.FIZZ, 1f, 1.5f)

            arrow.world.playEffect(arrow.location, Effect.EXPLOSION, 0, 30)
            arrow.remove()

            damaged.health = (damaged.health + healAmount).coerceAtMost(damaged.maxHealth)
        }
    }
}
