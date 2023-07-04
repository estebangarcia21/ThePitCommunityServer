package org.thepitcommunityserver.game.playerExperience

import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.thepitcommunityserver.db.data
import org.thepitcommunityserver.util.SECONDS
import org.thepitcommunityserver.util.Timer
import org.thepitcommunityserver.util.formatBracketsForLevel
import org.thepitcommunityserver.util.formatPitPlayerName

object PitName : Listener {
    private val chatTimer = Timer<Player>()

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val playerData = player.data

        setDisplayName(player, "${formatBracketsForLevel(playerData.level, playerData.prestige)} ${player.name}")
    }

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        event.isCancelled = true

        val chatSender = event.player

        chatTimer.cooldown(chatSender, 1 * SECONDS) {
            chatSender.world.players.forEach { target ->
                target.sendMessage("${formatPitPlayerName(chatSender)}: ${event.message}")
            }
        }
    }

    private fun setDisplayName(player: Player, name: String) {
        val handle = (player as CraftPlayer).handle

        handle.listName = CraftChatMessage.fromString(name)[0]

        fun sendNewPackets(t0: Player) {
            val t = (t0 as CraftPlayer).handle
            val connection = t.playerConnection

            connection.sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, t))
            connection.sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, t))
        }

        sendNewPackets(player)

        for (other in Bukkit.getOnlinePlayers()) {
            sendNewPackets(other)
        }
    }
}
