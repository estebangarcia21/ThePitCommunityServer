package org.thepitcommunityserver.game.events

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

object StopFireDamage : Listener {

    @EventHandler
    fun onFireDamage(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity !is Player) return

        when (event.cause) {
            EntityDamageEvent.DamageCause.FIRE,
            EntityDamageEvent.DamageCause.FIRE_TICK,
            EntityDamageEvent.DamageCause.LAVA -> {
                if (!entity.isInLava() && !entity.isInFire()) {
                    event.isCancelled = true
                    entity.fireTicks = 0 // Extinguish the player
                }
            }

            else -> {}
        }
    }

    private fun Player.isInFire(): Boolean {
        return getBlocksAtPlayer().any { it.type == Material.FIRE }
    }

    private fun Player.isInLava(): Boolean {
        return getBlocksAtPlayer().any { it.type.isLava() }
    }

    private fun Material.isLava(): Boolean {
        return this == Material.LAVA || this.name == "STATIONARY_LAVA"
    }

    private fun Player.getBlocksAtPlayer(): List<Block> {
        val loc = location
        val blocks = mutableListOf<Block>()

        for (x in -1..1) {
            for (z in -1..1) {
                for (y in 0..1) {
                    blocks.add(loc.clone().add(x * 0.3, y.toDouble(), z * 0.3).block)
                }
            }
        }

        return blocks.distinct()
    }
}