package org.thepitcommunityserver.game.playerExperience

import net.minecraft.server.v1_8_R3.PacketPlayOutTitle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.thepitcommunityserver.registerEvents
import org.thepitcommunityserver.util.createChatComponent
import org.thepitcommunityserver.util.replaceChatColorTags
import org.thepitcommunityserver.util.sendPacketToPlayer

object PlayerDeathMessage : Listener {
    @EventHandler
    fun playerDeathMessage(event: PlayerDeathEvent) {
        val player = event.entity ?: return

        val titleMessage = replaceChatColorTags("<red>YOU DIED!</red>")
        val title = createChatComponent(titleMessage)
        val titlePacket = PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, title, 0, 60, 15)

        sendPacketToPlayer(player, titlePacket)
    }
}
