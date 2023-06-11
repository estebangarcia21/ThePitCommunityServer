package org.thepitcommunityserver.game.experience

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.thepitcommunityserver.util.listToLocation
import org.thepitcommunityserver.util.getMapData

object Spawn : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        val mapData = getMapData()
        val spawnLoc = mapData.elemental.spawnPoints.random()

        player.teleport(listToLocation(player.world, spawnLoc))
    }
}
