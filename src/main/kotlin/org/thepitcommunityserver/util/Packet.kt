package org.thepitcommunityserver.util

import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.Packet
import net.minecraft.server.v1_8_R3.PacketListener
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

fun createChatPacket(text: String): PacketPlayOutChat {
    val message = "{\"text\":\"$text\"}"
    val serializer = IChatBaseComponent.ChatSerializer.a(message)
    val position: Byte = 2

    return PacketPlayOutChat(serializer, position)
}

fun <T: PacketListener> sendPacketToPlayer(player: Player, packet: Packet<T>) {
    val craftPlayer = player as? CraftPlayer ?: return

    craftPlayer.handle.playerConnection.sendPacket(packet)
}
