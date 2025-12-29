package org.thepitcommunityserver.game.playerExperience.player

import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.ChatColor
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.text.DecimalFormat

// TODO: Entire  file
object DamageIndicator : Listener {

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (event.entity !is Player) return
        val damager = event.damager as Player
        val damaged = event.entity as Player
        val damageTaken = event.finalDamage // damage in relation to all minecraft mechanics

        displayIndicator(damager, damaged, damageTaken)
    }

    private fun displayIndicator(damager: Player, damaged: Player, damageTaken: Double) {
        val packet = PacketPlayOutChat(
            IChatBaseComponent.ChatSerializer.a(
                "{\"text\":\"" +
                        buildIndicator(damaged, damageTaken) + "\"}"
            ), 2.toByte()
        )
        (damager as CraftPlayer).handle.playerConnection.sendPacket(packet)
    }

    private fun buildIndicator(damaged: Player, damageTaken: Double): String {
        val health = damaged.health.toInt() / 2
        val maxHealth = damaged.maxHealth.toInt() / 2
        val absorptionHearts = (damaged as CraftPlayer).handle.absorptionHearts.toInt() / 2
        val roundedDamageTaken = damageTaken.toInt()
        val indicatorString = StringBuilder()

        // TODO Get rank
        indicatorString.append(damaged.getName()).append(" ")
        for (i in 0 until Math.max(health - roundedDamageTaken, 0)) {
            indicatorString.append(ChatColor.DARK_RED.toString()).append("❤")
        }
        for (i in 0 until roundedDamageTaken - absorptionHearts) {
            indicatorString.append(ChatColor.RED.toString()).append("❤")
        }
        for (i in health until maxHealth) {
            indicatorString.append(ChatColor.BLACK.toString()).append("❤")
        }
        for (i in 0 until absorptionHearts) {
            indicatorString.append(ChatColor.YELLOW.toString()).append("❤")
        }
        indicatorString.append(ChatColor.RED.toString()).append(" ")
            .append(DecimalFormat("###0.000000000000000000000000000").format(damageTaken / 2)).append("HP")
        return indicatorString.toString()
    }
}