package org.thepitcommunityserver.util

import net.minecraft.server.v1_8_R3.*
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player


fun createChatComponent(text: String): IChatBaseComponent? {
    val message = "{\"text\":\"$text\"}"

    return IChatBaseComponent.ChatSerializer.a(message)
}

fun createChatPacket(message: String): PacketPlayOutChat {
    val serializer = createChatComponent(message)
    val position: Byte = 2

    return PacketPlayOutChat(serializer, position)
}

fun <T: PacketListener> sendPacketToPlayer(player: Player, packet: Packet<T>) {
    val craftPlayer = player as? CraftPlayer ?: return

    craftPlayer.handle.playerConnection.sendPacket(packet)
}
