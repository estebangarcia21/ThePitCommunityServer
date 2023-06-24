package org.thepitcommunityserver.game.events

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object NightVision : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        giveNightVision(event.player)
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        giveNightVision(event.player)
    }

    private fun giveNightVision(player: Player) {
        player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Int.MAX_VALUE, Integer.MAX_VALUE, true))
    }
}