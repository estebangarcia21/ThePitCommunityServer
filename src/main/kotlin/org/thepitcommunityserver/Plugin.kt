package org.thepitcommunityserver

import org.thepitcommunityserver.game.events.ArrowWatch
import org.thepitcommunityserver.game.events.BlockControl
import org.thepitcommunityserver.util.GlobalTimer

val lifecycleListeners = listOf(
    BlockControl,
    ArrowWatch,
    GlobalTimer
)

interface PluginLifecycleListener {
    fun onPluginEnable()
    fun onPluginDisable()
}
