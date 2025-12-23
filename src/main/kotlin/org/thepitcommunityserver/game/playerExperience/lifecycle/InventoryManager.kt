package org.thepitcommunityserver.game.playerExperience.lifecycle

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.util.*

object InventoryManager : Listener {

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        fun handlePermanentLossItems() {
            val inventory = event.entity.inventory

            val newArmorContents = arrayOfNulls<ItemStack>(4)
            inventory.armorContents.filter {
                hasNBTEntryFor(it.nbt, NBT.LOSE_ON_DEATH.key) && !hasNBTEntryFor(
                    it.nbt,
                    NBT.KEPT_ON_DEATH.key
                )
            }.forEachIndexed { i, item ->
                newArmorContents[i] = ItemStack(Material.AIR)
            }

            inventory.armorContents = newArmorContents
            inventory.contents.filter {
                hasNBTEntryFor(it.nbt, NBT.LOSE_ON_DEATH.key) && !hasNBTEntryFor(
                    it.nbt,
                    NBT.KEPT_ON_DEATH.key
                )
            }.forEach {
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
        handleUndroppableItems()
        handleRemoveOnDrop()
    }

    @EventHandler
    fun onPickupItem(event: PlayerPickupItemEvent) {
        val player = event.player
        val item = event.item.itemStack
        val material = item.type

        if (hasNBTEntryFor(item.nbt, NBT.DISABLE_PICKUP_EXISTS_IN_INVENTORY.key)) {
            if (playerHasItem(player, material)) {
                event.isCancelled = true
                return
            }
        }

        if (hasNBTEntryFor(item.nbt, NBT.AUTO_EQUIP.key) && isArmor(material)) {
            val currentPiece = getEquippedArmorPiece(player, material)
            val canOverride = currentPiece == null ||
                    hasNBTEntryFor(currentPiece.nbt, NBT.AUTO_EQUIP_OVERRIDABLE.key)

            if (canOverride && isArmorPieceStronger(player, material)) {
                equipArmorPiece(player, item, currentPiece)
                player.playSound(player.location, Sound.HORSE_ARMOR, 1f, 1f)
            } else {
                addItemToPlayerInventory(player, item)
                player.playSound(player.location, Sound.ITEM_PICKUP, 1f, 1f)
            }

            event.isCancelled = true
            event.item.remove()
            return
        }
    }
}
