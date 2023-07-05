package org.thepitcommunityserver.game.playerExperience.player

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_8_R3.ChatClickable
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.thepitcommunityserver.db.data
import org.thepitcommunityserver.util.*

object PlayerDeathMessage : Listener {

    @EventHandler
    fun playerDeathMessage(event: PlayerDeathEvent) {
        val player = event.entity ?: return

        val titleMessage = replaceChatColorTags("<red>YOU DIED!</red>")
        val title = createChatComponent(titleMessage)
        val titlePacket = PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, title, 0, 60, 15)

        var chatMessage = TextComponent("<red:bold>DEATH!</red:bold> by ${formatLevel(player.data.level, player.data.prestige)} ${player.name} <yellow:bold>VIEW RECAP</yellow:bold>".parseChatColors())
        chatMessage.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/recap")
        chatMessage.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder("<yellow>click to view kill recap<yellow>".parseChatColors()).create())

        sendPacketToPlayer(player, titlePacket)
        player.spigot().sendMessage(chatMessage)
    }
}
