
package org.thepitcommunityserver

import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import org.thepitcommunityserver.db.MemoryToDBSynchronizer
import org.thepitcommunityserver.game.commands.MysticEnchantCommand
import org.thepitcommunityserver.game.commands.SpawnCommand
import org.thepitcommunityserver.game.enchants.lib.ArmorChangeEventDispatcher
import org.thepitcommunityserver.game.enchants.lib.Enchants
import org.thepitcommunityserver.game.events.*
import org.thepitcommunityserver.game.playerExperience.lifecycle.DefaultArmor
import org.thepitcommunityserver.game.events.Spawn
import org.thepitcommunityserver.game.playerExperience.PitName
import org.thepitcommunityserver.game.playerExperience.PitScoreboard
import org.thepitcommunityserver.game.playerExperience.lifecycle.InventoryManager
import org.thepitcommunityserver.game.playerExperience.player.PlayerDeathMessage
import org.thepitcommunityserver.game.world.WorldHolograms
import org.thepitcommunityserver.util.CurrentWorld
import org.thepitcommunityserver.util.deregisterAllNPCs
import org.thepitcommunityserver.util.worldNPCS
import java.io.File

class Main : JavaPlugin {
    companion object {
        lateinit var plugin: JavaPlugin
    }

    @Suppress("unused")
    constructor() : super()

    @Suppress("unused")
    constructor(
        loader: JavaPluginLoader?,
        description: PluginDescriptionFile?,
        dataFolder: File?,
        file: File?
    ): super(loader, description, dataFolder, file)

    override fun onEnable() {
        plugin = this
        CurrentWorld.entities?.filter { it.type == EntityType.ARMOR_STAND }?.forEach {
            it.remove()
        }

        Bukkit.getLogger().info("--- ThePitCommunityServer ---")

        deregisterAllNPCs()

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
            MemoryToDBSynchronizer,
            DefaultArmor,
            PlayerDeathMessage,
            InventoryManager,
            WorldHolograms,
            PitName
        ).forEach(::registerEvents)

        enableGameRulesForDefaultWorld()

        plugin.getCommand(MysticEnchantCommand.name).executor = MysticEnchantCommand
        plugin.getCommand(SpawnCommand.name).executor = SpawnCommand

        lifecycleListeners.forEach(PluginLifecycleListener::onPluginEnable)

        worldNPCS.forEach { it.spawn() }
    }

    override fun onDisable() {
        lifecycleListeners.forEach(PluginLifecycleListener::onPluginDisable)

        deregisterAllNPCs()
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
}

fun registerEvents(listener: Listener) {
    Bukkit.getPluginManager().registerEvents(listener, Main.plugin)
}
