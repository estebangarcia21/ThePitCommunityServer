package org.thepitcommunityserver.game.events

import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.PluginLifecycleListener
import org.thepitcommunityserver.util.SECONDS
import org.thepitcommunityserver.util.Timer

object ArrowWatch : Listener, PluginLifecycleListener {
    private val arrows = HashMap<Arrow, ItemStack?>()
    private val timer = Timer<Unit>()

    @EventHandler
    fun onArrowShoot(event: EntityShootBowEvent) {
        val arrow = event.projectile as? Arrow ?: return
        val shooter = arrow.shooter as? Player ?: return

        arrows[arrow] = shooter.itemInHand
    }

    fun getBowFromArrow(arrow: Arrow): ItemStack? {
        return arrows[arrow]
    }

    /**
     * After every 15 seconds, free arrows that no longer exist in the world from the map.
     */
    private fun freeArrowsFromHashMap() {
        timer.after(Unit, 15 * SECONDS) {
            val obsoleteArrows = arrows.filterKeys { !it.isValid }

            obsoleteArrows.keys.forEach { arrow ->
                arrows.remove(arrow)
                arrow.remove()
            }

            freeArrowsFromHashMap()
        }
    }

    override fun onPluginEnable() {
        freeArrowsFromHashMap()
    }

    override fun onPluginDisable() {}
}
