package org.thepitcommunityserver.util

import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class HitCounter {

    private val timer = Timer<UUID>()
    private val cooldown = Time(3 * SECONDS)

    private val playerHitsWithEnchant = HashMap<UUID, Int>()

    fun addHit(player: Player) {
        val id = player.uniqueId

        timer.cooldown(id, cooldown.ticks(), resetTime = true) {
            val currentHits = playerHitsWithEnchant.getOrDefault(player.uniqueId, 0)
            playerHitsWithEnchant[id] = currentHits + 1
        }
    }
}