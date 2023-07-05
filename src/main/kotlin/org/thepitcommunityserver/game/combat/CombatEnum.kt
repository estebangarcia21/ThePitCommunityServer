package org.thepitcommunityserver.game.combat

import org.bukkit.ChatColor

enum class CombatEnum(val displayName: String) {
        COMBAT(ChatColor.RED.toString() + "Fighting"),
        IDLING(ChatColor.GREEN.toString() + "Idling"),
        BOUNTIED(ChatColor.RED.toString() + "Bountied")
    }

