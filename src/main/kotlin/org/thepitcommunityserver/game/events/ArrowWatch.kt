package org.thepitcommunityserver.game.events

import net.minecraft.server.v1_8_R3.ItemBow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.PluginLifecycleListener
import org.thepitcommunityserver.util.SECONDS
import org.thepitcommunityserver.util.Timer
import javax.swing.text.StyledEditorKit.BoldAction


data class ArrowContext(
    val isSneaking: Boolean,
    val bow: ItemStack?
)
object ArrowWatch : Listener, PluginLifecycleListener {
    private val arrows = HashMap<Arrow, ArrowContext>()
    private val timer = Timer<Unit>()


    @EventHandler
    fun onArrowShoot(event: EntityShootBowEvent) {
        val arrow = event.projectile


        if (arrow !is Arrow) return

        val shooter = arrow.shooter
        if (shooter !is Player) return

        arrows[arrow] = ArrowContext(bow = shooter.itemInHand, isSneaking = shooter.isSneaking)
    }

    fun getBowFromArrow(arrow: Arrow): ItemStack? {
        return arrows[arrow]?.bow
    }

    fun isArrowSneaking(arrow: Arrow): Boolean {
        return arrows[arrow]?.isSneaking ?: false
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
