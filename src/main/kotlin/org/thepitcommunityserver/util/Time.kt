package org.thepitcommunityserver.util

typealias Tick = Long

const val TICK = 1L
const val SECONDS = 20L * TICK
const val MINUTES = 60L * SECONDS
const val HOURS = 60L * MINUTES
const val DAYS = 24L * HOURS

fun Tick.ticks(): Long = this * TICK
fun Tick.seconds(): Long = this * SECONDS
fun Tick.minutes(): Long = this * MINUTES
fun Tick.hours(): Long = this * HOURS
fun Tick.days(): Long = this * DAYS
