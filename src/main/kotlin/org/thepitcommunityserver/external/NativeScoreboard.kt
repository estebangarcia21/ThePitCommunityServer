package org.thepitcommunityserver.external

/*
 * This class is taken from Exerosis's gist.
 * https://gist.github.com/Exerosis/f422c17dde154cca65cde4c4e4b43ca3
 *
 * This is NOT THE WORK of The Blue Hats development team. All credit goes to https://gist.github.com/Exerosis.
 *
 * This file was modified by The Blue Hats development team.
 */
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import java.util.*

class NativeScoreboard @JvmOverloads constructor(board: Scoreboard = Bukkit.getScoreboardManager().mainScoreboard) {
    private val teams = arrayOfNulls<Team>(maxLines)
    private val objective: Objective

    companion object {
        private var maxLines = 15
        private var maxCharacters = 0
        private val BLANKS = arrayOfNulls<String>(maxLines)

        init {
            maxCharacters = if (!Bukkit.getServer().version.contains("1.1")) 16 else 64
            for (i in 0 until maxLines) BLANKS[i] =
                String(charArrayOf(ChatColor.COLOR_CHAR, ('s'.toInt() + i).toChar()))
        }
    }

    fun title(title: Any) {
        objective.displayName = title.toString()
    }

    @JvmOverloads
    fun line(index: Int, text: String, score: Int = maxLines - index): Int {
        val max = maxCharacters
        val min = maxCharacters - 1
        val split = if (text.length < max) 0 else if (text[min] == '§') min else max
        teams[index]!!.prefix = if (split == 0) text else text.substring(0, split)
        teams[index]!!.suffix = if (split == 0) "" else text.substring(split)
        objective.getScore(BLANKS[index]).score = score
        return index
    }

    fun line(text: String): Int {
        for (i in 0 until maxLines) if (teams[i]!!.prefix.isNullOrEmpty()) return line(i, text)
        throw NoSuchElementException("No empty lines")
    }

    fun remove(index: Int): Boolean {
        if (index >= maxLines) return false
        objective.scoreboard.resetScores(BLANKS[index])
        return true
    }

    init {
        board.clearSlot(DisplaySlot.SIDEBAR)
        objective = board.registerNewObjective("sidebar", "dummy")
        objective.displaySlot = DisplaySlot.SIDEBAR
        for (i in 0 until maxLines) board.registerNewTeam(BLANKS[i]).also { teams[i] = it }
            .addEntry(BLANKS[i])
    }
}
