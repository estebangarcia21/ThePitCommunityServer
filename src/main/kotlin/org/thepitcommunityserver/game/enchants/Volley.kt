package org.thepitcommunityserver.game.enchants

import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityShootBowEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.ArrowWatch
import org.thepitcommunityserver.util.*
import org.thepitcommunityserver.util.Timer
import java.util.*

object Volley : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Volley",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = true,
            type = EnchantType.BOW,
            description
        )

    private val description: EnchantDescription = { "Shoot <white>${(arrows[it] ?: 0) + 1}</white> arrows at once" }

    private val arrows = mapOf(
        1 to 2,
        2 to 3,
        3 to 4,
    )

    private val timer = Timer<UUID>()
    private val activeVolleyShooters = mutableSetOf<Player>()

    @EventHandler
    fun onArrowShot(event: EntityShootBowEvent) {
        event.arrowShotWithEnchant(this, getBowOnShoot = true) {
            val arrow = it.arrow
            val shooter = it.shooter
            val enchantTier = it.enchantTier

            if (isInsideSpawn(shooter.location)) {
                return@arrowShotWithEnchant
            }

            if (shooter in activeVolleyShooters) {
                return@arrowShotWithEnchant
            }

            activeVolleyShooters.add(shooter)

            val arrowsToShoot = arrows[enchantTier] ?: undefPropErr("arrows", enchantTier)
            val originalVelocity = arrow.velocity
            val originalCritical = arrow.isCritical
            var arrowsFired = 1

            timer.after(
                id = arrow.uniqueId,
                ticks = arrowsToShoot * 2 * TICK,
                tickInterval = 2 * TICK,
                onTick = {
                    if (arrowsFired > arrowsToShoot) {
                        return@after
                    }

                    shooter.world.playSound(shooter.location, Sound.SHOOT_ARROW, 1f, 1f)
                    arrowsFired++

                    val volleyArrow = shooter.launchProjectile(Arrow::class.java)
                    volleyArrow.shooter = shooter
                    volleyArrow.velocity = shooter.eyeLocation.direction.normalize()
                        .multiply(originalVelocity.length())
                    volleyArrow.isCritical = originalCritical

                    ArrowWatch.registerArrow(volleyArrow, shooter.itemInHand, shooter.isSneaking)
                },
                operation = {
                    activeVolleyShooters.remove(shooter)
                }
            )
        }
    }


}