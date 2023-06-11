package org.thepitcommunityserver.game.experience

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.thepitcommunityserver.util.listToLocation
import org.thepitcommunityserver.util.getMapData

object Spawn : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        handleSpawn(event.player)
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        handleSpawn(event.player)
    }

    private fun handleSpawn(player: Player) {
        val mapData = getMapData()
        val spawnLoc = mapData.elemental.spawnPoints.random()

        player.teleport(listToLocation(player.world, spawnLoc))
    }
}
