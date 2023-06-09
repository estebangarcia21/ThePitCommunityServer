package org.thepitcommunityserver.game.enchants.lib

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.Main
import org.thepitcommunityserver.util.TICK
import org.thepitcommunityserver.util.Timer

data class PlayerArmorChangeEventContext(val player: Player, val enchantTier: Int)
typealias PlayerArmorChangeEventCallback = EventCallback<PlayerArmorChangeEventContext>

typealias ArmorContents = Array<out ItemStack?>

/**
 * Must call the `close` method to update the player's armor state.
 */
class ArmorChangeEvent(val player: Player) : Event() {
    companion object {
        @JvmStatic
        private val previousArmor: MutableMap<Player, ArmorContents> = mutableMapOf()

        @JvmStatic
        private val handlers = HandlerList()

        @Suppress("unused")
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }

    override fun getHandlers(): HandlerList {
        return ArmorChangeEvent.handlers
    }

    private var updatedState = false

    fun playerEquippedLeggingsWithEnchant(enchant: Enchant, callback: PlayerArmorChangeEventCallback) {
        val previousArmorContents = previousArmor[player]
        val currentArmorContents = player.inventory.armorContents

        val previousLeggings = previousArmorContents?.get(1)
        val currentLeggings = currentArmorContents[1]

        if (!isEmptyItemStack(currentLeggings) && isEmptyItemStack(previousLeggings)) {
            val enchantTier = getEnchantTierForItem(enchant, currentLeggings)
            if (enchantTier != null) {
                callback(PlayerArmorChangeEventContext(
                    player = player,
                    enchantTier = enchantTier
                ))
            }
        }
    }

    fun playerUnequippedLeggingsWithEnchant(enchant: Enchant, callback: PlayerArmorChangeEventCallback) {
        val previousArmorContents = previousArmor[player]
        val currentArmorContents = player.inventory.armorContents

        val previousLeggings = previousArmorContents?.get(1)
        val currentLeggings = currentArmorContents[1]

        if (!isEmptyItemStack(previousLeggings) && isEmptyItemStack(currentLeggings)) {
            val enchantTier = getEnchantTierForItem(enchant, previousLeggings)
            if (enchantTier != null) {
                callback(
                    PlayerArmorChangeEventContext(
                        player = player,
                        enchantTier = enchantTier
                    )
                )
            }
        }
    }

    fun stream(callback: (event: ArmorChangeEvent) -> Unit) {
        callback(this)
        close()
    }

    fun close() {
        if (updatedState) return

        previousArmor[player] = player.inventory.armorContents.clone()
        updatedState = true
    }
}

object ArmorChangeEventDispatcher : Listener {
    private val timer = Timer<Player>()

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        timer.after(player, 1 * TICK) {
            callArmorChangeEvent(ArmorChangeEvent(player))
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return

        timer.after(player, 1 * TICK) {
            callArmorChangeEvent(ArmorChangeEvent(player))
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        callArmorChangeEvent(ArmorChangeEvent(event.player))
    }
}

class LeggingsEnchantTracker(
    val enchant: Enchant,
    val onEquip: (player: Player, tier: Int) -> Unit,
    val onUnequip: (player: Player, tier: Int) -> Unit
) {
    fun bind(event: ArmorChangeEvent) {
        event.stream { e ->
            e.playerEquippedLeggingsWithEnchant(enchant) {
                onEquip(it.player, it.enchantTier)
            }

            e.playerUnequippedLeggingsWithEnchant(enchant) {
                onUnequip(it.player, it.enchantTier)
            }
        }
    }
}

fun callArmorChangeEvent(event: ArmorChangeEvent) {
    Main.plugin.server.pluginManager.callEvent(event)
}

fun isEmptyItemStack(itemStack: ItemStack?): Boolean {
    if (itemStack == null) return true

    return itemStack.type == Material.AIR
}
