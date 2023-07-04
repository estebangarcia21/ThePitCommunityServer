package org.thepitcommunityserver.game.enchants

import com.google.common.util.concurrent.AtomicDouble
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Chicken
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.util.Vector
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.ArrowWatch
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.util.TICK
import org.thepitcommunityserver.util.Timer
import org.thepitcommunityserver.util.isInsideSpawn
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object DevilChicks : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Devil Chicks",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = true,
            type = EnchantType.BOW,
            description
        )

    val word = mapOf(
        1 to "with",
        2 to "many",
        3 to "too many"
    )

    private val description: EnchantDescription = {
        if (it == 1) {
            "Arrows spawn an explosive chicken"
        } else {
            "Arrows spawn ${word[it]} explosive<br/>chickens"
        }
    }

    private val chickAmount = mapOf(
        1 to 1,
        2 to 2,
        3 to 3
    )

    private val timer = Timer<UUID>()

    @EventHandler
    fun onArrowLand(event: ProjectileHitEvent) {
        val arrow = event.entity
        val shooter = arrow.shooter

        if (arrow !is Arrow) return
        if (shooter !is Player) return

        if (isInsideSpawn(arrow.location)) return

        val bow = ArrowWatch.getBowFromArrow(arrow)
        val tier = getEnchantTierForItem(this, bow) ?: return

        spawnChicks(tier, shooter, arrow)
    }

    private fun spawnChicks(level: Int, shooter: Player, arrow: Projectile) {
        val world = shooter.world
        val shooterLocation = shooter.location
        val arrowLocation = arrow.location
        val chickenAmount = chickAmount[level] ?: error("Chicken amount can't be null.")

        val pitchIncrement = 0.15
        val volume = 0.5f
        val blastRadius = 0.75f
        val pitch = AtomicDouble(0.6)
        val animationIndex = AtomicInteger()
        val chickens = arrayOfNulls<Chicken>(chickenAmount)

        chickens.indices.forEach { i ->
            val direction = Vector()
            direction.x = arrowLocation.x + (Math.random() - Math.random()) * blastRadius
            direction.y = arrowLocation.y
            direction.z = arrowLocation.z + (Math.random() - Math.random()) * blastRadius

            val chicken = world.spawnEntity(direction.toLocation(arrowLocation.world), EntityType.CHICKEN) as Chicken
            chicken.setBaby()
            chickens[i] = chicken
        }

        fun playDevilChicksAnimation() {
            world.playSound(shooterLocation, Sound.NOTE_SNARE_DRUM, volume, pitch.get().toFloat())
            pitch.addAndGet(pitchIncrement)
            animationIndex.incrementAndGet()
        }

        timer.after(arrow.uniqueId, 10 * TICK, onTick = ::playDevilChicksAnimation) {
            world.playSound(shooterLocation, Sound.CHICKEN_HURT, 1f, 2f)

            chickens.filterNotNull().forEach { chicken ->
                chicken.getNearbyEntities(1.0, 1.0, 1.0).forEach { entity ->
                    if (entity is Player) {
                        DamageManager.applyTrueDamage(entity, shooter, 2.4)
                        createExplosion(entity, chicken.location)
                    }

                    world.playSound(arrowLocation, Sound.EXPLODE, volume, 1.6f)
                    world.playEffect(chicken.location, Effect.EXPLOSION_LARGE, Effect.EXPLOSION_LARGE.data, 100)
                }

                chicken.remove()
            }
        }
    }

    private fun createExplosion(target: Player, position: Location) {
        val explosion = target.location.toVector().subtract(position.toVector()).normalize()
        target.velocity = explosion
    }
}
