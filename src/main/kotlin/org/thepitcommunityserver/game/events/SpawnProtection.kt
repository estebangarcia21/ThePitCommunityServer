package org.thepitcommunityserver.game.events

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.thepitcommunityserver.util.currentMap
import org.thepitcommunityserver.util.isInsideRegion
import org.thepitcommunityserver.util.listToLocation
import org.thepitcommunityserver.util.world

object SpawnProtection : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.block.type != Material.OBSIDIAN) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (event.blockPlaced.type != Material.OBSIDIAN && event.blockPlaced.type != Material.COBBLESTONE) {
            event.isCancelled = true
            return
        }

        if (isInsideRegion(
                event.blockPlaced.location,
                listToLocation(world, currentMap.bounds.spawn.lower),
                listToLocation(world, currentMap.bounds.spawn.upper))) {
            event.isCancelled = true
        }
    }
}
