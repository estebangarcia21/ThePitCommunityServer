package org.thepitcommunityserver.game.playerExperience.lifecycle

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemFlag
import org.thepitcommunityserver.game.enchants.lib.isEmptyItemStack
import org.thepitcommunityserver.util.*
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

    private fun equipArmorContents(player: Player) {
        val inventory = player.inventory

        val random = Random.nextInt(3) // Randomly selects 0, 1, or 2

        val armorNBTs = mapOf(
            NBT.LOSE_ON_DEATH.entry,
            NBT.REMOVE_ON_DROP.entry,
            NBT.AUTO_EQUIP.entry,
            NBT.AUTO_EQUIP_OVERRIDABLE.entry
        )

        // Iron chestplate or chainmail chestplate.
        if (isEmptyItemStack(inventory.chestplate)) {
            val chestplateMaterial = if (random == 0) Material.IRON_CHESTPLATE else Material.CHAINMAIL_CHESTPLATE
            val chestplate = buildItem(
                chestplateMaterial,
                unbreakable = true,
                flags = listOf(ItemFlag.HIDE_UNBREAKABLE),
                nbtTags = armorNBTs
            )

            if (chestplateMaterial == Material.IRON_CHESTPLATE) {
                chestplate.nbt = removeNBTTag(chestplate.nbt, NBT.REMOVE_ON_DROP.key)
            }

            inventory.chestplate = chestplate
        }

        // Iron boots or chainmail boots.
        if (isEmptyItemStack(inventory.boots)) {
            val bootsMaterial = if (random == 1) Material.IRON_BOOTS else Material.CHAINMAIL_BOOTS
            val boots = buildItem(
                bootsMaterial,
                unbreakable = true,
                flags = listOf(ItemFlag.HIDE_UNBREAKABLE),
                nbtTags = armorNBTs
            )

            if (bootsMaterial == Material.IRON_BOOTS) {
                boots.nbt = removeNBTTag(boots.nbt, NBT.REMOVE_ON_DROP.key)
            }

            inventory.boots = boots
        }

        // Iron leggings or chainmail leggings.
        if (isEmptyItemStack(inventory.leggings)) {
            val leggingsMaterial = if (random == 2) Material.IRON_LEGGINGS else Material.CHAINMAIL_LEGGINGS
            val leggings = buildItem(
                leggingsMaterial,
                unbreakable = true,
                flags = listOf(ItemFlag.HIDE_UNBREAKABLE),
                nbtTags = armorNBTs
            )

            if (leggingsMaterial == Material.IRON_LEGGINGS) {
                leggings.nbt = removeNBTTag(leggings.nbt, NBT.REMOVE_ON_DROP.key)
            }

            inventory.leggings = leggings
        }

        // Iron sword.
        if (!inventory.contains(Material.IRON_SWORD)) {
            addItemToInventoryEmptySlot(inventory, buildItem(
                Material.IRON_SWORD,
                unbreakable = true,
                flags = listOf(ItemFlag.HIDE_UNBREAKABLE),
                nbtTags = mapOf(NBT.LOSE_ON_DEATH.entry, NBT.REMOVE_ON_DROP.entry)
            ))
        }

        // Bow.
        if (!inventory.contains(Material.BOW)) {
            addItemToInventoryEmptySlot(inventory, buildItem(
                Material.BOW,
                unbreakable = true,
                flags = listOf(ItemFlag.HIDE_UNBREAKABLE),
                nbtTags = mapOf(NBT.LOSE_ON_DEATH.entry, NBT.REMOVE_ON_DROP.entry)
            ))
        }

        // Arrows.
        if (!inventory.contains(Material.ARROW)) {
            addItemToInventoryEmptySlot(inventory, buildItem(
                Material.ARROW,
                count = 32,
                nbtTags = mapOf(NBT.LOSE_ON_DEATH.entry)
            ), preferredSlot = 8)
        }
    }
}
