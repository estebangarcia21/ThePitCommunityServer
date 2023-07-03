package org.thepitcommunityserver.game.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

object NoFallDamage : Listener {
    @EventHandler
    fun onFall(event: EntityDamageEvent) {
        event.isCancelled = event.cause == EntityDamageEvent.DamageCause.FALL
    }
}
