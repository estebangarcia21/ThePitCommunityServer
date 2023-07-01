package org.thepitcommunityserver.game.qol

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.thepitcommunityserver.db.data
import org.thepitcommunityserver.external.NativeScoreboard
import org.thepitcommunityserver.game.combat.CombatStatus
import org.thepitcommunityserver.util.CurrentWorld
import org.thepitcommunityserver.util.GlobalTimer
import org.thepitcommunityserver.util.SECONDS
import org.thepitcommunityserver.util.intToRoman
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object PitScoreboard : Listener {
    init {
        GlobalTimer.registerTask("scoreboard-updater", 1 * SECONDS) {
            CurrentWorld.players.forEach(::renderScoreboardView)
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) = renderScoreboardView(event.player)

    private fun renderScoreboardView(player: Player) {
        val scoreboard = Bukkit.getScoreboardManager().newScoreboard
        val optimizedBoard = NativeScoreboard(scoreboard)
        val simpleDateFormat = SimpleDateFormat("MM/dd/yy")
        val date = Date()
        val decimalFormat = DecimalFormat("#0.00")
        val playerData = player.data

        optimizedBoard.title(ChatColor.YELLOW.toString() + ChatColor.BOLD + "THE BLUE HATS PIT")
        optimizedBoard.line(appendColors(ChatColor.GRAY.toString() + simpleDateFormat.format(date) + " " + ChatColor.DARK_GRAY + "mega69L"))
        optimizedBoard.line(" ")

        if (player.data.prestige > 0) {
            optimizedBoard.line(formatLine("Prestige", ChatColor.YELLOW, intToRoman(playerData.prestige), false))
        }

        optimizedBoard.line(formatLine("Level", ChatColor.AQUA, "[${playerData.level}]", true))
        optimizedBoard.line(formatLine("Needed XP", ChatColor.AQUA, playerData.xp.toString(), false))
        optimizedBoard.line("  ")
        optimizedBoard.line(
            formatLine(
                "Gold",
                ChatColor.GOLD,
                decimalFormat.format(playerData.gold),
                false
            )
        )
        optimizedBoard.line("   ")
        optimizedBoard.line(formatLine("Status", null, formatStatus(player), false))
        optimizedBoard.line("    ")
        optimizedBoard.line(appendColors(ChatColor.YELLOW.toString() + "play.thebluehatspit.net"))

        player.scoreboard = scoreboard
    }

    private fun formatLine(key: String, color: ChatColor?, value: String, isBold: Boolean): String {
        val finalColor = color?.toString() ?: ""
        val boldColor = if (isBold) ChatColor.BOLD.toString() else ""
        val valueColor = finalColor + boldColor
        return appendColors(ChatColor.WHITE.toString() + key + ": " + valueColor + value)
    }

    /**
     * --- LEGACY ---
     * TODO: Investigate / rework this code.
     *
     * Adds color support to the NativeScoreboard implementation.
     */
    private fun appendColors(string: String): String {
        if (string.length >= 16) {
            val base = string.substring(0, 16)
            val end = string.substring(16)
            val colors = ArrayList<String>()

            run loop@ {
                (base.length - 1 downTo 0).forEach { i ->
                    if (base[i] == ChatColor.COLOR_CHAR) {
                        colors.add(base.substring(i, i + 2))

                        val charCheck = i - 2

                        if (charCheck < 0) return@forEach

                        if (base[charCheck] != ChatColor.COLOR_CHAR) return@loop
                    }
                }
            }

            val modifiers = StringBuilder()
            val colorsSize = colors.size

            (0 until colorsSize - 1).forEach { i ->
                modifiers.append(colors[i])
            }

            return base + colors[colorsSize - 1] + modifiers + end
        }

        return string
    }

    private fun formatStatus(player: Player): String {
        val combatStatus = CombatStatus.IDLING // TODO: Implement combat status.
        val formattedStatus = combatStatus.displayName

        return formattedStatus

//        return if (combatStatus === CombatStatus.COMBAT) formattedStatus + " " + ChatColor.RESET + ChatColor.GRAY + "(" + combatManager.getCombatTime(
//            player
//        ) + ")" else formattedStatus
    }
}
