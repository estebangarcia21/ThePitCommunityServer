package org.thepitcommunityserver.game.playerExperience.lifecycle

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.game.enchants.lib.isEmptyItemStack
import org.thepitcommunityserver.util.*

object InventoryManager : Listener {
    /**
     * Returns true if the player auto-equipped the item.
     */
    fun autoEquip(player: Player, item: ItemStack): Boolean {
        player.playSound(player.location, Sound.HORSE_SADDLE, 1f, 1f)

        val itemType = item.type
        val inventory = player.inventory

        var equippedItem = false

        if (isChestplate(itemType)) {
            if (isEmptyItemStack(inventory.chestplate) || hasNBTEntryFor(inventory.chestplate.nbt, NBT.AUTO_EQUIP_OVERRIDABLE.key)) {
                inventory.chestplate = item.clone()
                equippedItem = true
            }
        } else if (isLeggings(itemType)) {
            if (isEmptyItemStack(inventory.leggings) || hasNBTEntryFor(inventory.leggings.nbt, NBT.AUTO_EQUIP_OVERRIDABLE.key)) {
                inventory.leggings = item.clone()
                equippedItem = true
            }
        } else if (isBoots(itemType)) {
            if (isEmptyItemStack(inventory.boots) || hasNBTEntryFor(inventory.boots.nbt, NBT.AUTO_EQUIP_OVERRIDABLE.key)) {
                inventory.boots = item.clone()
                equippedItem = true
            }
        } else if (isHelmet(itemType)) {
            if (isEmptyItemStack(inventory.helmet) || hasNBTEntryFor(inventory.helmet.nbt, NBT.AUTO_EQUIP_OVERRIDABLE.key)) {
                inventory.helmet = item.clone()
                equippedItem = true
            }
        }

        return equippedItem
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        fun handlePermanentLossItems() {
            val inventory = event.entity.inventory

            val newArmorContents = arrayOfNulls<ItemStack>(4)
            inventory.armorContents.filter { hasNBTEntryFor(it.nbt, NBT.LOSE_ON_DEATH.key) }.forEachIndexed { i, item ->
                newArmorContents[i] = ItemStack(Material.AIR)
            }
            inventory.armorContents = newArmorContents

            inventory.contents.filter { hasNBTEntryFor(it.nbt, NBT.LOSE_ON_DEATH.key) }.forEach {
                inventory.remove(it)
            }

            event.entity.inventory.remove(Material.ARROW)
        }

        handlePermanentLossItems()
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val droppedItem = event.itemDrop.itemStack
        fun handleUndroppableItems() {
            if (!hasNBTEntryFor(droppedItem.nbt, NBT.UNDROPPABLE.key)) return

            event.isCancelled = true
        }

        fun handleRemoveOnDrop() {
            if (!hasNBTEntryFor(droppedItem.nbt, NBT.REMOVE_ON_DROP.key)) return

            event.itemDrop.remove()
        }

        handleRemoveOnDrop()
        handleUndroppableItems()
    }

    @EventHandler
    fun onPickupItem(event: PlayerPickupItemEvent) {
        val equipped = autoEquip(event.player, event.item.itemStack)
        if (equipped) {
            event.isCancelled = true
            event.item.remove()
        }
    }
}
