package org.thepitcommunityserver.util

import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.util.Vector
import org.thepitcommunityserver.registerEvents

private const val ARMOR_STAND_HEIGHT = 1.975

class Hologram(
    private var lines: List<String>,
    private val location: Location,
    private val lineHeight: Double = 0.3,
    private val offset: Vector = Vector(0, 0, 0)
) : Listener {
    private val entities = mutableListOf<ArmorStand>()

    init {
        registerEvents(this)
    }

    fun show() {
        removeCurrentLines()

        var baseLocation = location.clone().subtract(Vector(0.0, ARMOR_STAND_HEIGHT, 0.0)).add(offset)

        lines.reversed().forEach {
            displayHologramLine(it, baseLocation)
            baseLocation = baseLocation.add(Vector(0.0, lineHeight, 0.0))
        }
    }

    fun updateLines(newLines: List<String>) {
        lines = newLines
        show()
    }

    fun hide() {
        removeCurrentLines()
    }

    private fun removeCurrentLines() {
        entities.forEach { it.remove() }
        entities.clear()
    }

    private fun displayHologramLine(line: String, location: Location) {
        val entity = location.world.spawnEntity(location, EntityType.ARMOR_STAND) as ArmorStand

        entity.setGravity(false)
        entity.customName = line
        entity.canPickupItems = false
        entity.isCustomNameVisible = true
        entity.isVisible = false

        entities += entity
    }

    @EventHandler
    fun onArmorStandManipulation(e: PlayerArmorStandManipulateEvent) {
        if (!e.rightClicked.isVisible) {
            e.isCancelled = true
        }
    }
}
