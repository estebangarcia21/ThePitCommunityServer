package org.thepitcommunityserver.game.items.shopItems

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig
import org.thepitcommunityserver.util.parseChatColors
import org.thepitcommunityserver.util.playerRightClickItem
import java.util.*


object TacticalInsertion : Item {
    override val config: ItemConfig
        get() = ItemConfig(
            name = "Tactical Insertion",
            itemColor = "yellow",
            lore = listOf(
                "Sets your next spawnpoint where",
                "you stand.",
            ),
            material = Material.BLAZE_ROD,
        )

    val respawnLocations = mutableMapOf<UUID, Location>()

    @EventHandler
    fun onRightClick(event: PlayerInteractEvent) {
        event.playerRightClickItem {
            val player = it.player
            val item = it.item

            if (item.type != Material.BLAZE_ROD) return@playerRightClickItem

            if (item.amount > 1) {
                item.amount -= 1
            } else {
                player.inventory.remove(item)
            }

            player.sendMessage("<yellow><bold>GENIUS PLAY!</bold></yellow> Next respawn set here!".parseChatColors())
            if (respawnLocations.size > 0) {
                respawnLocations.remove(player.uniqueId)
            }
            respawnLocations[player.uniqueId] = player.location.clone()
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        respawnLocations.remove(event.player.uniqueId)
    }
}