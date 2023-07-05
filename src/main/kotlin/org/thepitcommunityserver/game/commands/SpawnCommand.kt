package org.thepitcommunityserver.game.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.thepitcommunityserver.util.*
import java.util.UUID

object SpawnCommand : CommandExecutor {
    const val name = "spawn"

    val timer = Timer<UUID>()
    val cooldown = Time(10L * SECONDS)

    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) return false
        if (!label.equals(name, ignoreCase = true)) return true

        if (isInsideSpawn(sender.location)) {
            sender.sendMessage("<red>You cannot /respawn here!</red>".parseChatColors())
            return true
        }

        if (timer.getCooldown(sender.uniqueId) != null) {
            sender.sendMessage("<red>You may only /respawn every 10 seconds!</red>".parseChatColors())
            return true
        }

        timer.cooldown(sender.uniqueId, cooldown.ticks()) {
            sender.teleport(randomSpawnLocation)
        }

        return true
    }
}
