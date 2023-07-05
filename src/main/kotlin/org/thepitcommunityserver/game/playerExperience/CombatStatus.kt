package org.thepitcommunityserver.game.playerExperience

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.thepitcommunityserver.game.combat.CombatStatusState
import org.thepitcommunityserver.game.enchants.lib.playerHitPlayer
import org.thepitcommunityserver.util.*

object CombatStatus : Listener {
    private val combatTimer = Timer<Player>()
    private val cooldown = Time(15L * SECONDS)

    private var playerCombatStatus = mutableMapOf<Player, CombatStatusState>()

    /**
     * Returns a pair with the combat status state and how long it will take to expire it.
     */
    fun getCombatStatus(player: Player): Pair<CombatStatusState, Tick> {
        val cooldown = combatTimer.getCooldown(player) ?: 0

        return playerCombatStatus.getOrPut(player) { CombatStatusState.IDLING } to Time(cooldown).seconds()
    }

    fun setCombatStatus(player: Player, status: CombatStatusState) {
        playerCombatStatus[player] = status
    }

    @EventHandler
    fun onPlayerJoin(event: Player) {
        playerCombatStatus[event.player] = CombatStatusState.IDLING // TODO: Or bounty.
    }

    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageByEntityEvent) {
        event.playerHitPlayer {
            val damager = it.damager
            val damaged = it.damaged

            if (isInsideSpawn(damager.location)) return@playerHitPlayer

            playerCombatStatus[damager] = CombatStatusState.COMBAT
            playerCombatStatus[damaged] = CombatStatusState.COMBAT

            combatTimer.after(damager, cooldown.ticks(), resetTime = true) {
                playerCombatStatus[damager] = CombatStatusState.IDLING
            }

            if (damager.uniqueId == damaged.uniqueId) return@playerHitPlayer

            combatTimer.after(damaged, cooldown.ticks(), resetTime = true) {
                playerCombatStatus[damaged] = CombatStatusState.IDLING
            }
        }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        playerCombatStatus[player] = CombatStatusState.IDLING
        // TODO Bountied
    }
}
