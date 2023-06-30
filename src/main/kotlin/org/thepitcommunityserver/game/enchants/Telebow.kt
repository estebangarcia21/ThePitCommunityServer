package org.thepitcommunityserver.game.enchants

import org.bukkit.ChatColor
import org.bukkit.Effect
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.thepitcommunityserver.game.enchants.lib.Enchant
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.ArrowWatch
import org.thepitcommunityserver.util.*
import org.thepitcommunityserver.util.Timer
import java.util.*
import kotlin.time.Duration.Companion.seconds

object Telebow : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Telebow",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = true,
            type = EnchantType.BOW
        ) {"Sneak to shoot a teleportation<br/>arrow (${cooldownTime[it]?.seconds()} cooldown, -3s per bow<br/>hit)" }

    private val cooldownTime = mapOf(
        1 to Time(90L * SECONDS),
        2 to Time(45L * SECONDS),
        3 to Time(20L * SECONDS)
    )

    private val timer = Timer<UUID>()
    private val cooldownReduction = Time(3L * SECONDS)

    @EventHandler
    fun onArrowLand(event: ProjectileHitEvent) {
        event.onArrowLand(this) {
            val arrow = it.arrow
            val shooter = it.shooter
            val cooldownTime = cooldownTime[it.enchantTier] ?: undefPropErr("cooldownTime", it.enchantTier)

            val isSneaking = ArrowWatch.isArrowSneaking(arrow)

            if (!isSneaking) return@onArrowLand
            if (isInsideSpawn(arrow.location)) return@onArrowLand

            timer.cooldown(shooter.uniqueId, cooldownTime.ticks()) {
                shooter.teleport(arrow)
                shooter.world.playEffect(shooter.location, Effect.CLOUD, 100)
                shooter.world.playSound(arrow.location, Sound.ENDERMAN_TELEPORT, 1f, 2f)
            }

            if (timer.getCooldown(shooter.uniqueId) != null) {
                sendCooldownMessage(shooter)
            }
        }
    }

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerArrowHitPlayerWithEnchant(this) {
            val damager = it.damager
            if (timer.getCooldown(damager.uniqueId) == null) return@damagerArrowHitPlayerWithEnchant

            timer.reduceCooldown(damager.uniqueId, cooldownReduction.ticks())
        }
    }

    private fun sendCooldownMessage(player: Player) {
        val cooldown = timer.getCooldown(player.uniqueId) ?: return
        val message = replaceChatColorTags("<yellow>Telebow:</yellow> <red>${cooldown/20}s cooldown!</red>")
        val packet = createChatPacket(message)

        sendPacketToPlayer(player, packet)
    }
}
