package org.thepitcommunityserver.util

class HitCounter<K> {
    private val timer = Timer<K>()
    private val cooldown = Time(1 * SECONDS)

    private val hits = HashMap<K, Int>()

    /**
     * When calling this function, 1 will be added to the hit counter. When n is reached in hits, the hits will go back
     * down to zero and the callback will be called.
     */
    fun onNthHit(id: K, n: Int, onHit: ((n: Int) -> Unit)? = null, onHitsReached: () -> Unit) {
        val currentHits = hits.getOrPut(id) { 0 }
        val updatedHits = currentHits + 1

        if (onHit != null) onHit(updatedHits)

        if (updatedHits == n - 1) {
            onHitsReached()
            hits.remove(id)
            timer.stop(id)
            return
        }

        hits[id] = updatedHits

        timer.cooldown(id, cooldown.ticks(), resetTime = true) {
            hits.remove(id)
        }
    }
}
