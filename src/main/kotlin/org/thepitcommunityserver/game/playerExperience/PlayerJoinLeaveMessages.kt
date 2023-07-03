package org.thepitcommunityserver.game.playerExperience

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.thepitcommunityserver.util.Text

object PlayerJoinLeaveMessages : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.joinMessage =
            (ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Welcome Back to The Pit Community Server! " + ChatColor.AQUA
                    + event.player.name + ChatColor.GREEN + ", thank you for coming back ${ChatColor.RED.toString() + Text.HEART}")
    }
}
