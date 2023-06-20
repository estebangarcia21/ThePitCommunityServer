package org.thepitcommunityserver.util

import org.bukkit.Bukkit
import org.thepitcommunityserver.Main

class Timer<K> {
    /**
     * The raw time amount for a player in ticks.
     */
    private val timer = mutableMapOf<K, Tick>()

    /**
     * Map of a player UUID to a Bukkit Task ID created by the scheduler.
     */
    private val tasks = mutableMapOf<K, Int>()

    val items: MutableSet<K>
        get() = timer.keys

    fun after(id: K, ticks: Tick, onTick: Runnable? = null, operation: Runnable) {
        setCooldown(id, ticks, false, onTick, operation)
    }

    fun cooldown(id: K, ticks: Tick, post: Runnable? = null, resetTime: Boolean = false, onTick: Runnable? = null, operation: Runnable) {
        val cooldown = getCooldown(id)
        if (cooldown == null) {
            operation.run()

            setCooldown(id, ticks, resetTime, onTick, post)
        }
    }

    fun setCooldown(id: K, time: Tick, resetTime: Boolean, onTick: Runnable?, post: Runnable?) {
        if (getCooldown(id) == null || resetTime) {
            timer[id] = time
        }

        if (tasks.contains(id)) return
        scheduleTimer(id, post, onTick)
    }

    fun getCooldown(id: K): Tick? {
        return timer[id]
    }

    fun stop(id: K) {
        val taskId = tasks[id] ?: return

        Bukkit.getScheduler().cancelTask(taskId)
        timer.remove(id)
        tasks.remove(id)
    }

     private fun scheduleTimer(id: K, post: Runnable?, onTick: Runnable? = null) {
        val rate = 1L
        val taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, {
            val currentTime = timer[id] ?: error("Attempted to schedule a timer with a null player UUID")
            val newTime = currentTime - rate
            val isFinalTick = newTime <= 0

            onTick?.run()

            if (isFinalTick) {
                stop(id)
                post?.run()
                return@scheduleSyncRepeatingTask
            }

            timer[id] = newTime
        }, 0L, rate)

        tasks[id] = taskId
    }
}
