package org.thepitcommunityserver

import org.thepitcommunityserver.game.events.ArrowWatch
import org.thepitcommunityserver.game.events.BlockControl

val lifecycleListeners = listOf<PluginLifecycleListener>(
    BlockControl,
    ArrowWatch
)

interface PluginLifecycleListener {
    fun onPluginEnable()
    fun onPluginDisable()
}
