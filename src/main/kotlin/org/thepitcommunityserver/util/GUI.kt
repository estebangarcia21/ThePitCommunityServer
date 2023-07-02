package org.thepitcommunityserver.util

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.game.enchants.lib.isEmptyItemStack
import org.thepitcommunityserver.registerEvents

data class ClickHandlerContext(
    val clickType: ClickType,
    val slot: Int,
    val rawSlot: Int
)

class GUI(
    title: String,
    rows: Int,
    contents: Map<Int, ItemStack?> = emptyMap(),
    private val onOpen: GUI.(player: Player) -> Unit = {},
    private val clickHandlers: Map<Int, (ctx: ClickHandlerContext) -> Unit> = emptyMap(),
    private val readOnly: Boolean = true,
) : Listener {
    private val gui: Inventory

    init {
        gui = Bukkit.createInventory(null, rows * 9, title)
        registerEvents(this)
        setContents(contents)
    }

    fun open(player: Player) {
        onOpen(player)
        player.openInventory(gui)
    }

    fun setContents(contents: Map<Int, ItemStack?>, targetPlayer: Player? = null) {
        contents.forEach { (slot, item) ->
            if (isEmptyItemStack(item)) {
                gui.setItem(slot, ItemStack(Material.AIR))
            } else {
                gui.setItem(slot, item)
            }
        }

        targetPlayer?.updateInventory()
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val eventGUI = event.clickedInventory
        if (eventGUI != gui) return

        if (readOnly) {
            event.isCancelled = true
        }

        val ctx = ClickHandlerContext(
            clickType = event.click,
            slot = event.slot,
            rawSlot = event.rawSlot
        )

        clickHandlers[ctx.rawSlot]?.let { it(ctx) }
    }
}
