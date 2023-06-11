
package org.thepitcommunityserver

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.thepitcommunityserver.game.commands.MysticEnchantCommand
import org.thepitcommunityserver.game.enchants.lib.Enchants
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.game.events.NoFallDamage
import org.thepitcommunityserver.game.events.PlayerJoinLeaveMessages
import org.thepitcommunityserver.game.experience.Spawn

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

        listOf(
            Spawn,
            NoFallDamage,
            PlayerJoinLeaveMessages
        ).forEach(::registerEvents)

        enableGameRulesForDefaultWorld()

        plugin.getCommand(MysticEnchantCommand.name).executor = MysticEnchantCommand
    }

    private fun enableGameRulesForDefaultWorld() {
        Bukkit.getWorlds().forEach {
            it.setGameRuleValue("doFireTick", "false")
            it.setGameRuleValue("doImmediateFireSpread", "false")
            it.setGameRuleValue("doMobSpawning", "false")
            it.setGameRuleValue("keepInventory", "true")
        }
    }

    private fun registerEvents(listener: Listener) {
        Bukkit.getPluginManager().registerEvents(listener, this)
    }
}
