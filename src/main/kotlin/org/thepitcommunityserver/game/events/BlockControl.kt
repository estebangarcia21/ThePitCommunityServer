package org.thepitcommunityserver.game.events

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.thepitcommunityserver.PluginLifecycleListener
import org.thepitcommunityserver.util.MINUTES
import org.thepitcommunityserver.util.Timer
import org.thepitcommunityserver.util.isInsideSpawn

object BlockControl : Listener, PluginLifecycleListener {
    private val timer = Timer<Block>()

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val block = event.blockPlaced

        if (isInsideSpawn(block.location) || block.type != Material.OBSIDIAN) return

        timer.after(block, 2 * MINUTES) { block.type = Material.AIR }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block

        if (block.type != Material.OBSIDIAN) return

        timer.stop(block)
    }

    override fun onPluginEnable() {}

    override fun onPluginDisable() {
        timer.items.forEach { it.type = Material.AIR }
    }
}
