package org.thepitcommunityserver.game.enchants

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.*

object Assassin : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Assassin",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = true,
            type = EnchantType.PANTS,
            {"Sneaking teleports you behind<br/>your attacker ${cooldownTime[it]?.seconds()}s cooldown)"}
        )

    private val cooldownTime = mapOf(
        1 to Time(5L * SECONDS),
        2 to Time(4L * SECONDS),
        3 to Time(3L * SECONDS)
    )

    private val timer = Timer()

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagedReceivedAnyHitWithPantsEnchant(this) {damager, damaged, tier, _, ->
            val cooldownTime = cooldownTime[tier] ?: undefPropErr("cooldownTime", tier)

            if (!damaged.isSneaking) return@damagedReceivedAnyHitWithPantsEnchant

            timer.cooldown(damaged.uniqueId, cooldownTime.ticks()) {
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