package org.thepitcommunityserver.game.enchants

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.ArrowWatch
import org.thepitcommunityserver.util.*
import org.thepitcommunityserver.util.Timer
import java.util.*

object Assassin : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Assassin",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = true,
            type = EnchantType.PANTS,
            description
        )

    private val description: EnchantDescription = {
        if (it == 1) {
            "Sneaking teleports you behind<br/>players bowing you. (${cooldown[it]?.seconds()}s cooldown)"
        } else {
            "Sneaking teleports you behind your<br/>attacker. (${cooldown[it]?.seconds()}s cooldown)"
        }
    }
    private val timer = Timer<UUID>()
    private val cooldown = mapOf(
        1 to Time(10L * SECONDS),
        2 to Time(5L * SECONDS),
        3 to Time(3L * SECONDS)
    )

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagedReceivedAnyHitWithPantsEnchant(this) {
            val damager = it.damager
            val damaged = it.damaged
            val arrow = it.arrow
            val cooldown = cooldown[it.enchantTier] ?: undefPropErr("cooldown", it.enchantTier)

            // If tier 1, only teleport if damaged by bow
            if (arrow != null && it.enchantTier == 1) {
                val bow = ArrowWatch.getBowFromArrow(arrow)
                if (bow == null) return@damagedReceivedAnyHitWithPantsEnchant
            }

            if (!damaged.isSneaking) return@damagedReceivedAnyHitWithPantsEnchant

            timer.cooldown(damaged.uniqueId, cooldown.ticks()) {
                val tpLoc = damager.location.subtract(damager.eyeLocation.direction.normalize())
                tpLoc.y = damager.location.y

                if (tpLoc.block.type == Material.AIR) {
                    damaged.teleport(tpLoc)
                } else {
                    damaged.teleport(damager)
                }

                damaged.world.playSound(damaged.location, Sound.ENDERMAN_TELEPORT, 1f, 2f)
            }
        }
    }
}