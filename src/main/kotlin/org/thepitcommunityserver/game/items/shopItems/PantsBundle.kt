package org.thepitcommunityserver.game.items.shopItems

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig
import org.thepitcommunityserver.util.NBT

object PantsBundle : Item {
    override val config: ItemConfig
        get() = ItemConfig(
            name = "Pants Bundle",
            material = Material.MINECART,
            lore = listOf("Hold and right-click to store 10", "pairs of fresh pair of pants."),
            nbtTags = mapOf(NBT.KEPT_ON_DEATH.entry),
            itemColor = "aqua",
        )

    var description = mutableListOf("")

//    private val fillerSlots = listOf(0, 1, 7, 8, 9, 10, 16, 17)
//
//    private val fillerItem = buildItem(
//        material = Material.STAINED_GLASS_PANE,
//        name = "",
//        data = 15
//    )
//
//    private val storageSlots = listOf(2, 3, 4, 5, 6, 11, 12, 13, 14, 15)  // The 10 usable slots
//    private val playerStorage = mutableMapOf<String, Array<ItemStack?>>()
//
//    private fun getStorage(playerUUID: String): Array<ItemStack?> {
//        return playerStorage.getOrPut(playerUUID) { arrayOfNulls(10) }
//    }
//
//    private fun hasStoredItems(playerUUID: String): Boolean {
//        return getStorage(playerUUID).any { it != null && it.type != Material.AIR }
//    }
//
//    private fun updateBundleMaterial(player: Player) {
//        val heldItem = player.itemInHand ?: return
//        if (heldItem.type != Material.MINECART && heldItem.type != Material.STORAGE_MINECART) return
//
//        val newMaterial = if (hasStoredItems(player.uniqueId.toString())) {
//            Material.STORAGE_MINECART
//        } else {
//            Material.MINECART
//        }
//
//        if (heldItem.type != newMaterial) {
//            heldItem.type = newMaterial
//            player.updateInventory()
//        }
//    }
//
//    fun createGUI(player: Player): GUI {
//        return GUI(
//            title = "Pants Bundle",
//            rows = { 2 },
//            readOnly = false,
//            lockedSlots = fillerSlots.toSet(),
//            onOpen = { player ->
//                // Set filler items
//                val contents = mutableMapOf<Int, ItemStack?>()
//                fillerSlots.forEach { slot ->
//                    contents[slot] = fillerItem
//                }
//
//                // Load stored items into storage slots
//                val storage = getStorage(player.uniqueId.toString())
//                storageSlots.forEachIndexed { index, slot ->
//                    contents[slot] = storage[index]
//                }
//
//                setContents(contents)
//            },
//            onClose = { player ->
//                // Save storage slots back
//                val inv = player.openInventory.topInventory
//                val storage = getStorage(player.uniqueId.toString())
//
//                storageSlots.forEachIndexed { index, slot ->
//                    storage[index] = inv.getItem(slot)
//                }
//                // Update the held item's material based on stored items' Ignore the filler slots
//                updateBundleMaterial(player)
//
//            },
//            clickHandlers = fillerSlots.associateWith {
//                { ctx: ClickHandlerContext -> }
//            }.toMutableMap()
//        )
//    }

    @EventHandler
    fun onRightClick(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return

        if (event.item.type == Material.MINECART) {
            val player = event.player

            val contents = player.inventory.contents
            var freshPants = mutableListOf<ItemStack>()

            for (item in contents) {
                if (item.type == Material.LEATHER_HELMET) {
                    freshPants.add(item)
                }
            }

            if (freshPants.size < 10) {
                player.sendMessage("§cYou need at least 10 fresh pairs of pants to use the Pants Bundle!")
                return
            }

            // Remove 10 fresh pants from inventory
            var pantsToRemove = 10
            for (i in contents.indices) {
                val item = contents[i]
                if (item.type == Material.LEATHER_HELMET && pantsToRemove > 0) {
                    val amountToRemove = minOf(item.amount, pantsToRemove)
                    item.amount -= amountToRemove
                    pantsToRemove -= amountToRemove
                    if (item.amount <= 0) {
                        contents[i] = null
                    }
                }
            }

            description.removeLast()

            description
        }


    }
}