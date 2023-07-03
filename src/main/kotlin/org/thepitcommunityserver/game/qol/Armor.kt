package org.thepitcommunityserver.game.qol

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.game.enchants.lib.isEmptyItemStack
import org.thepitcommunityserver.util.buildItem

object ArmorController : Listener {
    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        equipArmorContents(event.player)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        equipArmorContents(event.player)
    }

    private fun equipArmorContents(player: Player) {
        val inventory = player.inventory

        if (isEmptyItemStack(inventory.chestplate)) inventory.chestplate = ItemStack(Material.IRON_CHESTPLATE)
        if (isEmptyItemStack(inventory.boots)) inventory.boots = ItemStack(Material.IRON_BOOTS)
        if (isEmptyItemStack(inventory.leggings)) inventory.leggings = ItemStack(Material.CHAINMAIL_LEGGINGS)

        if (!inventory.contains(Material.GOLD_SWORD) && !inventory.contains(Material.DIAMOND_SWORD) && !inventory.contains(
                Material.IRON_SWORD
            )
        ) inventory.addItem(
            buildItem(material = Material.IRON_SWORD, unbreakable = true)
        )
    }
}
