package org.thepitcommunityserver.game.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.thepitcommunityserver.util.Timer
import org.thepitcommunityserver.util.isInsideSpawn
import org.thepitcommunityserver.util.parseChatColors
import java.util.*

object OofCommand: CommandExecutor {
    const val name = "oof"

    private val timer = Timer<UUID>()

    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) return false
        if (!label.equals(name, ignoreCase = true)) return true

        if (isInsideSpawn(sender.location)) {
            sender.sendMessage("<red><bold>NOPE!</bold></red> Can't /oof in spawn!".parseChatColors())

            return true
        }

        if (timer.getCooldown(sender.uniqueId) != null) {
            sender.sendMessage("<red><bold>CHILL OUT!</bold></red> You may only /oof every 10 seconds!".parseChatColors())

            return true
        }

        timer.cooldown(sender.uniqueId, 200) {
            sender.health = 0.0
        }

        return true
    }
}
