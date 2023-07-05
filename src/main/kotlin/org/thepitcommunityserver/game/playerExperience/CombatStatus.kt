package org.thepitcommunityserver.game.playerExperience

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.combat.CombatEnum
import org.thepitcommunityserver.game.enchants.lib.playerHitPlayer
import org.thepitcommunityserver.util.SECONDS
import org.thepitcommunityserver.util.Time
import org.thepitcommunityserver.util.Timer
import java.util.*

object CombatStatus : Listener {
    private val timer = Timer<UUID>()
    private val cooldown = Time(15L * SECONDS)

    var playerCombatStatus = mutableMapOf<UUID, CombatEnum>()

    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageByEntityEvent) {
        event.playerHitPlayer {
            val damager = it.damager
            val damaged = it.damaged
            println("balls")

            playerCombatStatus[damager.uniqueId] = CombatEnum.COMBAT
            playerCombatStatus[damaged.uniqueId] = CombatEnum.COMBAT

            timer.cooldown(damager.uniqueId, cooldown.ticks(), resetTime = true) {
                playerCombatStatus[damager.uniqueId] = CombatEnum.IDLING

            }
            timer.cooldown(damaged.uniqueId, cooldown.ticks(), resetTime = true) {
                playerCombatStatus[damaged.uniqueId] = CombatEnum.IDLING
            }
        }
    }
}




