package org.thepitcommunityserver.game.playerExperience.lifecycle

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.thepitcommunityserver.util.NBT
import org.thepitcommunityserver.util.hasNBTEntryFor
import org.thepitcommunityserver.util.nbt

object InventoryManager : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        fun handlePermanentLossItems() {
            val inventory = event.entity.inventory
            inventory.contents.filter { hasNBTEntryFor(it.nbt, NBT.LOSE_ON_DEATH.key) }.forEach {
                inventory.remove(it)
            }

            event.entity.inventory.remove(Material.ARROW)
        }

        handlePermanentLossItems()
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        fun handleUndroppableItems() {
            if (!hasNBTEntryFor(event.itemDrop.itemStack.nbt, NBT.UNDROPPABLE.key)) return

            event.isCancelled = true
        }

        handleUndroppableItems()
    }
}
