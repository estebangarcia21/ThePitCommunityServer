
package org.thepitcommunityserver

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.thepitcommunityserver.game.commands.MysticEnchantCommand
import org.thepitcommunityserver.game.enchants.DevilChicks
import org.thepitcommunityserver.game.enchants.Gamble
import org.thepitcommunityserver.game.enchants.Mirror
import org.thepitcommunityserver.game.enchants.lib.Enchants
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.game.events.PlayerJoinLeaveMessages

@Suppress("unused")
class Main : JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
    }

    override fun onEnable() {
        plugin = this

        Bukkit.getLogger().info("--- ThePitCommunityServer ---")

        // Register enchantments.
        Enchants.forEach(::registerEvents)

        registerEvents(DamageManager)
        registerEvents(PlayerJoinLeaveMessages)

        plugin.getCommand(MysticEnchantCommand.name).executor = MysticEnchantCommand
    }

    private fun registerEvents(listener: Listener) {
        Bukkit.getPluginManager().registerEvents(listener, this)
    }
}
