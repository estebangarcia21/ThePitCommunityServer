
package org.thepitcommunityserver

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.thepitcommunityserver.db.MemoryToDBSynchronizer
import org.thepitcommunityserver.game.commands.MysticEnchantCommand
import org.thepitcommunityserver.game.enchants.lib.ArmorChangeEventDispatcher
import org.thepitcommunityserver.game.enchants.lib.Enchants
import org.thepitcommunityserver.game.events.*
import org.thepitcommunityserver.game.experience.Spawn
import org.thepitcommunityserver.game.qol.PitScoreboard

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

        // Game events.
        listOf(
            Spawn,
            NoFallDamage,
            PlayerJoinLeaveMessages,
            HungerControl,
            StopLiquidFlow,
            SpawnProtection,
            BlockControl,
            ClearArrows,
            ArrowWatch,
            NightVision,
            ArmorChangeEventDispatcher,
            PitScoreboard,
            MemoryToDBSynchronizer
        ).forEach(::registerEvents)

        enableGameRulesForDefaultWorld()

        plugin.getCommand(MysticEnchantCommand.name).executor = MysticEnchantCommand

        lifecycleListeners.forEach(PluginLifecycleListener::onPluginEnable)
    }

    override fun onDisable() {
        lifecycleListeners.forEach(PluginLifecycleListener::onPluginDisable)
    }

    private fun enableGameRulesForDefaultWorld() {
        Bukkit.getWorlds().forEach { world ->
            mapOf(
                "doFireTick" to "false",
                "doImmediateFireSpread" to "false",
                "doMobSpawning" to "false",
                "keepInventory" to "true",
                "doDaylightCycle" to "false"
            ).entries.forEach { (key, value) -> world.setGameRuleValue(key, value) }

            world.time = 1000
        }
    }

    private fun registerEvents(listener: Listener) {
        Bukkit.getPluginManager().registerEvents(listener, this)
    }
}
