package org.thepitcommunityserver.game.playerExperience

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerJoinEvent

object HungerControl : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        player.maxHealth = 20.0
        player.foodLevel = 19
    }

    @EventHandler
    fun onHunger(event: FoodLevelChangeEvent) {
        event.isCancelled = true
    }
}
