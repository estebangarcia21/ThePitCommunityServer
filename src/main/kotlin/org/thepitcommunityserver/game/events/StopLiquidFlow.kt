package org.thepitcommunityserver.game.events

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockFromToEvent

object StopLiquidFlow : Listener {
    @EventHandler
    fun onBlockToEvent(event: BlockFromToEvent) {
        if (event.block.type == Material.STATIONARY_WATER || event.block.type == Material.STATIONARY_LAVA) {
            event.isCancelled = true
        }
    }
}
