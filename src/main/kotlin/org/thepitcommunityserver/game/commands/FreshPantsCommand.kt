package org.thepitcommunityserver.game.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.thepitcommunityserver.game.items.mysticItems.MysticPants
import org.thepitcommunityserver.game.items.mysticItems.PantsColor
import org.thepitcommunityserver.util.parseChatColors

object FreshPantsCommand : PluginCommand {
    override val name = "freshpants"

    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if (sender !is Player) return false
        if (!label.equals(name, ignoreCase = true)) return true

        val colorArg = args?.getOrNull(0)?.uppercase()
        val color = colorArg?.let {
            PantsColor.entries.find { c -> c.name == it }
        } ?: PantsColor.entries.random()

        val pants = MysticPants.buildFreshPants(sender, color)
        sender.inventory.addItem(pants)
        sender.sendMessage("<green>You received Fresh ${color.displayName} Pants!</green>".parseChatColors())

        return true
    }
}