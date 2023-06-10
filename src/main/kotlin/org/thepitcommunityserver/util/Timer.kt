package org.thepitcommunityserver.util

import org.bukkit.Bukkit
import org.thepitcommunityserver.Main
import java.util.*

class Timer {
    /**
     * The raw time amount for a player in ticks.
     */
    private val timer = mutableMapOf<UUID, Tick>()

    /**
     * Map of a player UUID to a Bukkit Task ID created by the scheduler.
     */
    private val tasks = mutableMapOf<UUID, Int>()

    fun cooldown(uuid: UUID, time: Tick, post: Runnable? = null, resetTime: Boolean = false, operation: Runnable) {
        val cooldown = getCooldown(uuid)
        if (cooldown == null) {
            operation.run()

            setCooldown(uuid, time, resetTime, post)
        }
    }

    fun setCooldown(uuid: UUID, time: Tick, resetTime: Boolean, post: Runnable?) {
        if (getCooldown(uuid) == null || resetTime) {
            timer[uuid] = time
        }

        if (tasks.contains(uuid)) return
        scheduleTimer(uuid, post)
    }

    fun getCooldown(uuid: UUID): Tick? {
        return timer[uuid]
    }

    fun removeCooldown(uuid: UUID) {
        timer.remove(uuid)

        val taskId = tasks[uuid] ?: return
        Bukkit.getScheduler().cancelTask(taskId)
    }

     private fun scheduleTimer(uuid: UUID, post: Runnable?) {
        val rate = 1L
        val taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, {
            val currentTime = timer[uuid] ?: error("Attempted to schedule a timer with a null player UUID")
            val newTime = currentTime - rate

            if (newTime <= 0) {
                removeCooldown(uuid)
                post?.run()
                return@scheduleSyncRepeatingTask
            }

            timer[uuid] = newTime
        }, 0L, rate)

        tasks[uuid] = taskId
    }
}
