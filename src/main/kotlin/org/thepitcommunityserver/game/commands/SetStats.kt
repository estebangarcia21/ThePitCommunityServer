package org.thepitcommunityserver.game.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.thepitcommunityserver.db.data

object SetStats : PluginCommand {

    override val name = "set"

    override fun onCommand(sender: CommandSender?, command: Command?, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        if (args.size != 2) {
            sender.sendMessage("Usage: /set <gold|xp> <amount>")
            return true
        }

        val stat = args[0].lowercase()
        val amountString = args[1]

        if (stat != "gold" && stat != "xp") {
            sender.sendMessage("You can only set 'gold' or 'xp' stats.")
            return true
        }

        val player = sender.data

        when (stat) {
            "gold" -> {
                val goldAmount = amountString.toDoubleOrNull()

                if (goldAmount == null || goldAmount < 0) {
                    sender.sendMessage("Please provide a valid amount of gold.")
                    return true
                }

                if (goldAmount > 1_000_000_000) {
                    sender.sendMessage("Gold amount cannot exceed 1,000,000,000.")
                    return true
                }

                player.gold = goldAmount
                sender.sendMessage("Your gold has been set to ${goldAmount.toLong()}.")
            }
            "xp" -> {
                val xpAmount = amountString.toIntOrNull()

                if (xpAmount == null || xpAmount < 0) {
                    sender.sendMessage("Please provide a valid amount of XP.")
                    return true
                }

                if (xpAmount > 1_000_000_000) {
                    sender.sendMessage("XP amount cannot exceed 1,000,000,000.")
                    return true
                }

                player.xp = xpAmount
                sender.sendMessage("Your XP has been set to $xpAmount.")
            }
        }

        return true
    }
}