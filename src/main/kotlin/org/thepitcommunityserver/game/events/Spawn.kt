package org.thepitcommunityserver.game.events

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import org.thepitcommunityserver.util.*

object Spawn : Listener {
    private val respawnTimer = Timer<Unit>()

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.teleport(randomSpawnLocation)
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        event.player.teleport(randomSpawnLocation)
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        event.deathMessage = ""
        handleAutoSpawn(event.entity)
    }

    private fun handleAutoSpawn(player: Player) {
        player.health = player.maxHealth
        player.teleport(randomSpawnLocation)

        respawnTimer.after(Unit, 1L * TICK) {
            for (effect in player.activePotionEffects) {
                player.removePotionEffect(effect.type)
            }
            player.foodLevel = 19
            player.health = player.maxHealth

            player.asCraftPlayer {
                it.handle.absorptionHearts = 0f
            }

            player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Int.MAX_VALUE, 0))
            player.velocity = Vector(0, 0, 0)
            player.fireTicks = 0
        }
    }
}
