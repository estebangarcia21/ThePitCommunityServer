package org.thepitcommunityserver.game.events

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.thepitcommunityserver.util.isInsideSpawn

object SpawnProtection : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.block.type != Material.OBSIDIAN) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val block = event.blockPlaced

        if (block.type != Material.OBSIDIAN && block.type != Material.COBBLESTONE) {
            event.isCancelled = true
            return
        }

        if (isInsideSpawn(block.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBucketEmpty(event: PlayerBucketEmptyEvent) {
        val blockLocation = event.blockClicked.location

        if (isInsideSpawn(blockLocation)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onHit(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        val damaged = event.entity

        if (isInsideSpawn(damager.location) || isInsideSpawn(damaged.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onProjectileShoot(event: EntityShootBowEvent) {
        if (!isInsideSpawn(event.projectile.location)) return

        event.isCancelled = true
    }
}
