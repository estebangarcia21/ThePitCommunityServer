package org.thepitcommunityserver.game.enchants

import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
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

    private val timer = Timer<UUID>()
    private val cooldownTime = mapOf(
        1 to Time(90L * SECONDS),
        2 to Time(45L * SECONDS),
        3 to Time(20L * SECONDS)
    )

    private val cooldownReduction = Time(3L * SECONDS)

    @EventHandler
    fun onArrowLand(event: ProjectileHitEvent) {
        event.onArrowLand(this) {
            val arrow = it.arrow
            val shooter = it.shooter
            val cooldownTime = cooldownTime[it.enchantTier] ?: undefPropErr("cooldownTime", it.enchantTier)

            timer.cooldown(shooter.uniqueId, cooldownTime.ticks()) {
                val isSneaking = ArrowWatch.isArrowSneaking(arrow)

                if (!isSneaking) return@cooldown

                shooter.teleport(arrow)
                shooter.world.playSound(arrow.location, Sound.ENDERMAN_TELEPORT, 1f, 2f)
            }
        }
    }

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent){
            event.damagerArrowHitPlayerWithEnchant(this){
                val damager = it.damager
                if (timer.getCooldown(damager.uniqueId) == null) return@damagerArrowHitPlayerWithEnchant

                timer.reduceCooldown(damager.uniqueId, cooldownReduction.ticks())
            }
    }

    @EventHandler
    fun onArrowShoot(event: EntityShootBowEvent) {
        event.arrowShotWithEnchant(this) {
            val shooter = it.shooter
            val cooldown = timer.getCooldown(shooter.uniqueId)

            if (!shooter.isSneaking) return@arrowShotWithEnchant
            if (cooldown == null) return@arrowShotWithEnchant

            val packet = PacketPlayOutChat(
                IChatBaseComponent.ChatSerializer.a(
                    "{\"text\":\""
                            + ChatColor.RED + "Telebow Cooldown: " + cooldown.seconds + "(s)" + "\"}"
                ),
                2.toByte()
            )
            (shooter as CraftPlayer).handle.playerConnection.sendPacket(packet)
        }
    }
}