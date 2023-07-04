package org.thepitcommunityserver.game.world

import org.bukkit.event.Listener
import org.bukkit.util.Vector
import org.thepitcommunityserver.util.CurrentWorldConfig
import org.thepitcommunityserver.util.Hologram
import org.thepitcommunityserver.util.replaceChatColorTags
import org.thepitcommunityserver.util.toLocation

object WorldHolograms : Listener {
    private val centerHologram = Hologram(
        lines = listOf(
            "<yellow>The Blue Hats Pit</yellow>",
            "<green:bold>JUMP!</green:bold> <red:bold>FIGHT!</red:bold>"
        ).map(::replaceChatColorTags),
        location = CurrentWorldConfig.centerHologram.toLocation()
    )

    init {
        listOf(
            centerHologram,
            Hologram(
                lines = listOf(
                    "<light-purple:bold>MYSTIC WELL</light-purple:bold>",
                    "<gray>Item Enchants</gray>"
                ).map(::replaceChatColorTags),
                location = CurrentWorldConfig.mysticWell.toLocation(),
                offset = Vector(0.0, 1.0, 0.0)
            ),
            Hologram(
                lines = listOf(
                    "<dark-purple:bold>ENDER CHEST</dark-purple:bold>",
                    "<gray>Store items forever</gray>"
                ).map(::replaceChatColorTags),
                location = CurrentWorldConfig.enderChest.toLocation(),
                offset = Vector(0.0, 1.0, 0.0)
            )
        ).forEach { it.show() }
    }
}
