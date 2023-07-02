package org.thepitcommunityserver.game.qol

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import org.thepitcommunityserver.db.data
import org.thepitcommunityserver.game.combat.CombatStatus
import org.thepitcommunityserver.util.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object PitScoreboard : Listener {
    private val scoreboards = mutableMapOf<Player, FlickerlessScoreboard>()

    init {
        GlobalTimer.registerTask("scoreboard-updater", 1 * SECONDS) {
            CurrentWorld.players.forEach(::renderScoreboardView)
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) = renderScoreboardView(event.player)

    private fun renderScoreboardView(player: Player) {
        val board = scoreboards.getOrPut(player) { FlickerlessScoreboard(Bukkit.getScoreboardManager().newScoreboard) }

        val simpleDateFormat = SimpleDateFormat("MM/dd/yy")
        val date = Date()
        val decimalFormat = DecimalFormat("#0.00")

        player.data.apply {
            gold += 10
        }
        val playerData = player.data

        board.title(ChatColor.YELLOW.toString() + ChatColor.BOLD + "THE BLUE HATS PIT")
        board.line(ChatColor.GRAY.toString() + simpleDateFormat.format(date) + " " + ChatColor.DARK_GRAY + "mega69L")
        board.emptyLine()

        if (player.data.prestige > 0) {
            board.line(formatLine("Prestige", ChatColor.YELLOW, intToRoman(playerData.prestige)))
        }

        board.line(formatLine("Level", null, formatLevel(playerData.level, playerData.prestige)))
        board.line(
            formatLine(
                "Needed XP",
                ChatColor.AQUA,
                getRequiredXPForLevel(player.data.level, player.data.prestige).toString(),
            )
        )
        board.line("  ")
        board.line(
            formatLine(
                "Gold",
                ChatColor.GOLD,
                decimalFormat.format(playerData.gold),
            )
        )
        board.emptyLine()
        board.line(formatLine("Status", null, formatStatus(player)))
        board.emptyLine()
        board.line(ChatColor.YELLOW.toString() + "play.thebluehatspit.net")

        board.update()

        player.scoreboard = board.scoreboard
    }

    private fun formatLevel(level: Int, prestigeLevel: Int): String {
        val prestigeColor = getPrestigeColor(prestigeLevel)
        val chatColor = getChatColorForLevel(level)
        val formattedLevel = if (level >= 60) "${ChatColor.BOLD}$level" else level.toString()

        return "${ChatColor.RESET}${prestigeColor}[${chatColor}$formattedLevel${prestigeColor}]"
    }

    private fun formatLine(key: String, color: ChatColor?, value: String): String {
        val finalColor = color?.toString() ?: ""
        return ChatColor.WHITE.toString() + key + ": " + finalColor + value
    }

    private fun formatStatus(player: Player): String {
        val combatStatus = CombatStatus.IDLING // TODO: Implement combat status.

        return combatStatus.displayName

//        return if (combatStatus === CombatStatus.COMBAT) formattedStatus + " " + ChatColor.RESET + ChatColor.GRAY + "(" + combatManager.getCombatTime(
//            player
//        ) + ")" else formattedStatus
    }
}

private const val MAX_LINES = 15
private const val MAX_CHARS = 16

class FlickerlessScoreboard(val scoreboard: Scoreboard) {

    private val objective: Objective
    private val teams = mutableMapOf<Int, Team>()

    private var updateIndex = 0

    fun update() {
        updateIndex = 0
    }

    fun title(title: String) {
        objective.displayName = title
    }

    fun line(text: String) {
        val team = teams[updateIndex] ?: return

        val (prefix, suffix) = splitStringInMiddle(segmentColors(text))
        team.prefix = prefix
        team.suffix = suffix

        objective.getScore(getScoreIndex(updateIndex)).score = MAX_LINES - updateIndex
        updateIndex++
    }

    fun emptyLine() {
        fun fillEmptyString(length: Int, character: Char): String {
            return String(CharArray(length) { character })
        }

        line(fillEmptyString(updateIndex, ' '))
    }

    private fun getScoreIndex(index: Int): String {
        return String(charArrayOf(ChatColor.COLOR_CHAR, ('s'.toInt() + index).toChar()))
    }

    init {
        scoreboard.clearSlot(DisplaySlot.SIDEBAR)
        objective = scoreboard.registerNewObjective("sidebar", "dummy")
        objective.displaySlot = DisplaySlot.SIDEBAR

        val initialTeams = arrayOfNulls<String>(MAX_LINES)
        initialTeams.forEachIndexed { i, _ ->
            val scoreIndex = getScoreIndex(i)
            scoreboard.registerNewTeam(scoreIndex).also { teams[i] = it }.addEntry(scoreIndex)
        }
    }

    private fun segmentColors(string: String): String {
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
}

fun splitStringInMiddle(input: String): Pair<String, String> {
    val maxLength = MAX_CHARS
    return if (input.length > maxLength) {
        val firstHalf = input.substring(0, maxLength)
        val secondHalf = input.substring(maxLength)
        Pair(firstHalf, secondHalf)
    } else {
        Pair(input, "")
    }
}
