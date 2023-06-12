package org.thepitcommunityserver.util

typealias Tick = Long

const val TICK = 1L
const val SECONDS = 20L * TICK
const val MINUTES = 60L * SECONDS
const val HOURS = 60L * MINUTES
const val DAYS = 24L * HOURS

/**
 * Inspired by golang's `time.Duration` interface.
 */
class Time(private val tick: Tick) {
    fun ticks(): Long = tick
    fun seconds(): Long = tick / SECONDS
    fun minutes(): Long = tick / MINUTES
    fun hours(): Long = tick / HOURS
    fun days(): Long = tick / DAYS
}
