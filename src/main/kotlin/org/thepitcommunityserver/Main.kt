
package org.thepitcommunityserver

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Main : JavaPlugin() {
    override fun onEnable() {
        Bukkit.getLogger().info("--- Successfully started ThePitCommunityServer ---")

        super.onEnable()
    }
}
