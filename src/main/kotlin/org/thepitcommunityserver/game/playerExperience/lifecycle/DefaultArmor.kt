package org.thepitcommunityserver.game.playerExperience.lifecycle

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.thepitcommunityserver.game.enchants.lib.isEmptyItemStack
import org.thepitcommunityserver.util.addItemToInventoryEmptySlot
import org.thepitcommunityserver.util.buildItem

object DefaultArmor : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        equipArmorContents(event.entity)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        equipArmorContents(event.player)
    }

    private fun equipArmorContents(player: Player) {
        val inventory = player.inventory

        if (isEmptyItemStack(inventory.chestplate)) inventory.chestplate = buildItem(
            Material.IRON_CHESTPLATE,
            unbreakable = true
        )
        if (isEmptyItemStack(inventory.boots)) inventory.boots = buildItem(Material.IRON_BOOTS, unbreakable = true)
        if (isEmptyItemStack(inventory.leggings)) inventory.leggings = buildItem(Material.CHAINMAIL_LEGGINGS, unbreakable = true)

        if (!inventory.contains(Material.IRON_SWORD)) {
            addItemToInventoryEmptySlot(inventory, buildItem(Material.IRON_SWORD, unbreakable = true))
        }

        if (!inventory.contains(Material.BOW)) {
            addItemToInventoryEmptySlot(inventory, buildItem(Material.BOW, unbreakable = true))
        }

        if (!inventory.contains(Material.ARROW)) {
            addItemToInventoryEmptySlot(inventory, buildItem(Material.ARROW, count = 32))
        }
    }
}
