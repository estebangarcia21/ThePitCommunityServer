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
    val player: Player,
    val clickType: ClickType,
    val slot: Int,
    val rawSlot: Int
)

class GUI(
    private val title: String,
    private val rows: (Player) -> Int = { 5 }, // Dynamic rows
    private val contents: Map<Int, ItemStack?> = emptyMap(),
    private val onOpen: GUI.(player: Player) -> Unit = {},
    private val onClose: (player: Player) -> Unit = {},
    private val clickHandlers: MutableMap<Int, (ctx: ClickHandlerContext) -> Unit> = mutableMapOf(),
    private val onClickBuilder: (GUI.(Player) -> Map<Int, (ctx: ClickHandlerContext) -> Unit>)? = null, // Dynamic click handlers
    private val readOnly: Boolean = true,
    private val lockedSlots: Set<Int> = emptySet(),
) : Listener {
    private var gui: Inventory? = null
    private var activeClickHandlers: Map<Int, (ctx: ClickHandlerContext) -> Unit> = clickHandlers

    init {
        registerEvents(this)
    }

    val size: Int
        get() = gui?.size ?: 0

    val lastSlot: Int
        get() = size - 1

    fun open(player: Player) {
        gui = Bukkit.createInventory(null, rows(player) * 9, title)
        setContents(contents)

        onClickBuilder?.let { builder ->
            activeClickHandlers = clickHandlers + builder(player)
        }

        onOpen(player)
        player.openInventory(gui)
    }

    fun setContents(contents: Map<Int, ItemStack?>, targetPlayer: Player? = null) {
        val inventory = gui ?: return
        contents.forEach { (slot, item) ->
            if (isEmptyItemStack(item)) {
                inventory.setItem(slot, ItemStack(Material.AIR))
            } else {
                inventory.setItem(slot, item)
            }
        }
        targetPlayer?.updateInventory()
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val eventGUI = event.clickedInventory
        if (eventGUI != gui) return

        if (event.rawSlot in lockedSlots) {
            event.isCancelled = true
            return
        }

        if (event.click.isShiftClick && event.clickedInventory == event.whoClicked.inventory) {
            event.isCancelled = true
            return
        }

        if (readOnly) {
            event.isCancelled = true
        }

        val ctx = ClickHandlerContext(
            player = event.whoClicked as? Player ?: return,
            clickType = event.click,
            slot = event.slot,
            rawSlot = event.rawSlot
        )

        activeClickHandlers[ctx.rawSlot]?.invoke(ctx)
    }

    @EventHandler
    fun onInventoryClose(event: org.bukkit.event.inventory.InventoryCloseEvent) {
        if (event.inventory != gui) return
        val player = event.player as? Player ?: return
        onClose(player)
    }
}
