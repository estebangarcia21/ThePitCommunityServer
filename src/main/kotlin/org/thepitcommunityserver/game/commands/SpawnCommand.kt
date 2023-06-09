package org.thepitcommunityserver.game.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.thepitcommunityserver.util.Timer
import org.thepitcommunityserver.util.isInsideSpawn
import org.thepitcommunityserver.util.parseChatColors
import org.thepitcommunityserver.util.randomSpawnLocation
import java.util.UUID

object SpawnCommand : CommandExecutor {
    const val name = "spawn"

    private val timer = Timer<UUID>()

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

        timer.cooldown(sender.uniqueId, 200) {
            sender.teleport(randomSpawnLocation)
        }

        return true
    }
}
