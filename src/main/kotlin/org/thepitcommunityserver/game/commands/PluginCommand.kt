package org.thepitcommunityserver.game.commands

import org.bukkit.command.CommandExecutor

interface PluginCommand : CommandExecutor {
    val name: String
}