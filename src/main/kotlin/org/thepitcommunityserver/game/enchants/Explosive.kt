package org.thepitcommunityserver.game.enchants

import org.bukkit.Effect
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.util.*
import org.thepitcommunityserver.util.Timer
import java.util.*

object Explosive : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Explosive",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = true,
            type = EnchantType.BOW,
            description
        )


    private val description: EnchantDescription = {
        val words = mapOf(
            1 to "POP",
            2 to "BOOM",
            3 to "BOOM"
        )
        "Arrows go ${words[it]}! (${cooldown[it]}s cooldown)"
    }

    private val damageAmount = mapOf(
        1 to 1.0,
        2 to 2.0,
        3 to 3.0
    )

    private val range = mapOf(
        1 to 1.0,
        2 to 2.5,
        3 to 6.0
    )

    private val explosionPitch = mapOf(
        1 to 2f,
        2 to 1.5f,
        3 to 1.2f
    )

    private val explosionParticle = mapOf(
        1 to Effect.EXPLOSION_LARGE,
        2 to Effect.EXPLOSION_HUGE,
        3 to Effect.EXPLOSION_HUGE
    )

    private val timer = Timer<UUID>()
    private val cooldown = mapOf(
        1 to Time(5L * SECONDS),
        2 to Time(3L * SECONDS),
        3 to Time(5L * SECONDS)
    )

    @EventHandler
    fun onArrowLand(event: ProjectileHitEvent) {
        event.onArrowLand(this) {
            val arrow = it.arrow
            val shooter = it.shooter

            val location = arrow.location

            // Prevent spawn Explosion
            if (isInsideSpawn(location)) return@onArrowLand


            val damageAmount = damageAmount[it.enchantTier] ?: undefPropErr("damageAmount", it.enchantTier)
            val enchantTier = it.enchantTier
            val range = range[enchantTier] ?: undefPropErr("range", enchantTier)
            val explosionPitch = explosionPitch[enchantTier] ?: undefPropErr("explosionPitch", enchantTier)
            val explosionParticle = explosionParticle[enchantTier] ?: undefPropErr("explosion)Particle", enchantTier)

            timer.cooldown(shooter.uniqueId, cooldown[enchantTier]!!.ticks()) {

                arrow.world.playSound(location, Sound.EXPLODE, 0.75f, explosionPitch)
                arrow.world.playEffect(location, explosionParticle, explosionParticle.data, 100)

                arrow.getNearbyEntities(range, range, range).forEach { entity ->

                    // Only affect players
                    if (entity !is Player) return@forEach

                    // Prevent self damage
                    if (entity !== shooter) return@forEach

                    val force = entity.location.toVector().subtract(location.toVector()).normalize().multiply(1.25)
                    force.setY(.85f)
                    entity.velocity = force
                    DamageManager.applyTrueDamage(entity, shooter, damageAmount)
                }
            }
        }
    }
}
