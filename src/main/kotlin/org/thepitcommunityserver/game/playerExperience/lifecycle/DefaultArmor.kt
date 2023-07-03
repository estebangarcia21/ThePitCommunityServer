package org.thepitcommunityserver.game.playerExperience.lifecycle

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemFlag
import org.thepitcommunityserver.game.enchants.lib.isEmptyItemStack
import org.thepitcommunityserver.util.NBT
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

        // Iron chestplate.
        if (isEmptyItemStack(inventory.chestplate)) inventory.chestplate = buildItem(
            Material.IRON_CHESTPLATE,
            unbreakable = true,
            flags = listOf(ItemFlag.HIDE_UNBREAKABLE),
            nbtTags = mapOf(NBT.LOSE_ON_DEATH.entry)
        )

        // Iron boots.
        if (isEmptyItemStack(inventory.boots)) inventory.boots = buildItem(
            Material.IRON_BOOTS,
            unbreakable = true,
            flags = listOf(ItemFlag.HIDE_UNBREAKABLE),
            nbtTags = mapOf(NBT.LOSE_ON_DEATH.entry)
        )

        // Iron leggings.
        if (isEmptyItemStack(inventory.leggings)) inventory.leggings = buildItem(
            Material.CHAINMAIL_LEGGINGS,
            unbreakable = true,
            flags = listOf(ItemFlag.HIDE_UNBREAKABLE),
            nbtTags = mapOf(NBT.LOSE_ON_DEATH.entry)
        )

        // Iron sword.
        if (!inventory.contains(Material.IRON_SWORD)) {
            addItemToInventoryEmptySlot(inventory, buildItem(
                Material.IRON_SWORD,
                unbreakable = true,
                flags = listOf(ItemFlag.HIDE_UNBREAKABLE),
                nbtTags = mapOf(NBT.LOSE_ON_DEATH.entry)
            ))
        }

        // Bow.
        if (!inventory.contains(Material.BOW)) {
            addItemToInventoryEmptySlot(inventory, buildItem(
                Material.BOW,
                unbreakable = true,
                flags = listOf(ItemFlag.HIDE_UNBREAKABLE),
                nbtTags = mapOf(NBT.LOSE_ON_DEATH.entry)
            ))
        }

        // Arrows.
        if (!inventory.contains(Material.ARROW)) {
            addItemToInventoryEmptySlot(inventory, buildItem(
                Material.ARROW,
                count = 32
            ), preferredSlot = 8)
        }
    }
}
