
package org.thepitcommunityserver

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.thepitcommunityserver.game.enchants.Gamble
import org.thepitcommunityserver.game.enchants.Mirror
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.game.events.PlayerJoinLeaveMessages

@Suppress("unused")
class Main : JavaPlugin() {
    override fun onEnable() {
        Bukkit.getLogger().info("--- ThePitCommunityServer ---")

        // Register enchantments.
        listOf(
            Gamble,
            Mirror
        ).forEach(::registerEvents)

        registerEvents(DamageManager)
        registerEvents(PlayerJoinLeaveMessages)
    }

    private fun registerEvents(listener: Listener) {
        Bukkit.getPluginManager().registerEvents(listener, this)
    }
}
