package org.thepitcommunityserver.game.playerExperience.lifecycle

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.thepitcommunityserver.game.enchants.lib.isEmptyItemStack
import org.thepitcommunityserver.game.items.defaultItems.*
import org.thepitcommunityserver.util.addItemToInventoryEmptySlot
import kotlin.random.Random

object DefaultArmor : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        equipArmorContents(event.entity)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        equipArmorContents(event.player)
    }

    private fun equipArmorContents(player: Player, disableSpawnItems: Boolean = false) {
        val inventory = player.inventory
        val random = Random.nextInt(3)

        if (isEmptyItemStack(inventory.chestplate)) {
            val chestplate = if (random == 0) IronChestplate else ChainmailChestplate
            inventory.chestplate = chestplate.build(player)
        }

        if (isEmptyItemStack(inventory.boots)) {
            val boots = if (random == 1) IronBoots else ChainmailBoots
            inventory.boots = boots.build(player)
        }

        if (isEmptyItemStack(inventory.leggings)) {
            val leggings = if (random == 2) IronLeggings else ChainmailLeggings
            inventory.leggings = leggings.build(player)
        }

        if (disableSpawnItems) {
            return
        }

        if (!inventory.contains(Material.IRON_SWORD)) {
            addItemToInventoryEmptySlot(inventory, IronSword.build(player))
        }

        if (!inventory.contains(Material.BOW)) {
            addItemToInventoryEmptySlot(inventory, Bow.build(player))
        }

        if (!inventory.contains(Material.ARROW)) {
            addItemToInventoryEmptySlot(inventory, Arrows.build(player), preferredSlot = 8)
        }
    }
}