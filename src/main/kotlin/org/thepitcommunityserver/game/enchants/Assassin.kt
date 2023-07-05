package org.thepitcommunityserver.game.enchants

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
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
            {"Sneaking teleports you behind<br/>your attacker. (${cooldownTime[it]?.seconds()}s cooldown)"}
        )

    private val cooldownTime = mapOf(
        1 to Time(10L * SECONDS),
        2 to Time(5L * SECONDS),
        3 to Time(3L * SECONDS)
    )

    private val timer = Timer<UUID>()

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagedReceivedAnyHitWithPantsEnchant(this) {
            val cooldownTime = cooldownTime[it.enchantTier] ?: undefPropErr("cooldownTime", it.enchantTier)

            if (!it.damaged.isSneaking) return@damagedReceivedAnyHitWithPantsEnchant

            timer.cooldown(it.damaged.uniqueId, cooldownTime.ticks()) {
                val tpLoc = it.damager.location.subtract(it.damager.eyeLocation.direction.normalize())
                tpLoc.y = it.damager.location.y

                if (tpLoc.block.type == Material.AIR) {
                    it.damaged.teleport(tpLoc)
                } else {
                    it.damaged.teleport(it.damager)
                }

                it.damaged.world.playSound(it.damaged.location, Sound.ENDERMAN_TELEPORT, 1f, 2f)
            }
        }
    }
}