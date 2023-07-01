package org.thepitcommunityserver

import org.thepitcommunityserver.game.events.ArrowWatch
import org.thepitcommunityserver.game.events.BlockControl
import org.thepitcommunityserver.game.qol.PitScoreboard
import org.thepitcommunityserver.util.GlobalTimer

val lifecycleListeners = listOf<PluginLifecycleListener>(
    BlockControl,
    ArrowWatch,
    GlobalTimer
)

interface PluginLifecycleListener {
    fun onPluginEnable()
    fun onPluginDisable()
}
