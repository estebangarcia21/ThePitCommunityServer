package org.thepitcommunityserver.game.enchants

import com.google.common.util.concurrent.AtomicDouble
import org.bukkit.Bukkit
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Chicken
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.util.Vector
import org.thepitcommunityserver.Main
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.util.SECONDS
import org.thepitcommunityserver.util.TICK
import org.thepitcommunityserver.util.Time
import org.thepitcommunityserver.util.Timer
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

    private val description: EnchantDescription = {
        val word = mapOf(
            1 to "with",
            2 to "many",
            3 to "too many"
        )[it]

        "Arrows spawn with $word explosive chickens."
    }

    private val chickAmount = mapOf(
        1 to 1,
        2 to 2,
        3 to 3
    )

    private val devilChickAnimations = HashMap<UUID, Int>()

    private val timer = Timer()

    @EventHandler
    fun onArrowLand(event: ProjectileHitEvent) {
        val projectile = event.entity
        val shooter = projectile.shooter

        if (shooter is Player) {
            val bow = shooter.inventory.itemInHand
            val tier = getEnchantTierForItem(this, bow) ?: return

            spawnChicks(tier, shooter, projectile)
        }
    }

    // TODO: Legacy code, cleanup in the future?
    // TODO: Force remove chickens after certain time.
    private fun spawnChicks(level: Int, shooter: Player, arrow: Projectile) {
        val world = shooter.world
        val arrowUuid = UUID.randomUUID()
        val shooterLocation = shooter.location
        val arrowLocation = arrow.location
        val chickenAmount = chickAmount[level] ?: error("Chicken amount can't be null.")

        val pitchIncrement = 0.1
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

        timer.after(arrowUuid, Time(10 * TICK).ticks()) {
            if (animationIndex.get() == 10) {
                Bukkit.getScheduler().cancelTask(devilChickAnimations[arrowUuid]!!)
                devilChickAnimations.remove(arrowUuid)
                world.playSound(shooterLocation, Sound.CHICKEN_HURT, 1f, 2f)

                chickens.forEach { chicken -> chicken!!.getNearbyEntities(1.0, 1.0, 1.0).forEach { entity ->
                    if (entity is Player) {
                        DamageManager.applyTrueDamage(entity, shooter, 2.4)
                        createExplosion(entity, chicken.location)
                    }

                    world.playSound(arrowLocation, Sound.EXPLODE, volume, 1.6f)
                    world.playEffect(chicken.location, Effect.EXPLOSION_LARGE, Effect.EXPLOSION_LARGE.data, 100)
                    chicken.remove()
                } }
            }

            world.playSound(shooterLocation, Sound.NOTE_SNARE_DRUM, volume, pitch.get().toFloat())
            pitch.addAndGet(pitchIncrement)
            animationIndex.incrementAndGet()
        }
    }

    private fun createExplosion(target: Player, position: Location) {
        val explosion = target.location.toVector().subtract(position.toVector()).normalize()
        target.velocity = explosion
    }
}
