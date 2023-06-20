package org.thepitcommunityserver.game.events

import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import java.util.UUID

object ClearArrows : Listener {
    private val arrows = HashMap<UUID, MutableList<Arrow>>()

    @EventHandler
    fun onArrowShoot(event: EntityShootBowEvent) {
        val arrow = event.projectile

        if (arrow !is Arrow) return

        val shooter = arrow.shooter
        if (shooter !is Player) return

        val arrowStack = arrows.getOrPut(shooter.uniqueId) { mutableListOf() }
        arrowStack.add(arrow)

        // If the stack exceeds the limit of 7, remove the oldest arrow.
        if (arrowStack.size > 7) {
            val removedArrow = arrowStack.removeAt(0)
            removedArrow.remove()
        }
    }
}
