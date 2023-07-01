package org.thepitcommunityserver.db

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.thepitcommunityserver.util.*
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException
import java.util.*

private const val SYNC_TIME: Tick = 30 * MINUTES
private val memoryPlayerData = mutableMapOf<UUID, DBPlayer>()

val DEBUG_DB = System.getenv("DEBUG_DB")?.toBoolean() ?: false
fun sendDBDebugMessage(player: Player, msg: String) {
    player.sendMessage(
        replaceChatColorTags("<yellow><bold>[Debug/DB]</bold></yellow> $msg")
    )
}

object MemoryToDBSynchronizer : Listener {
    init {
        GlobalTimer.registerTask("memory-to-db-sync", SYNC_TIME) {
            CurrentWorld.players.forEach {
                if (DEBUG_DB) sendDBDebugMessage(it, "Synced in-memory data to the database")

                syncMemoryPlayerToDB(it.uniqueId)
            }
        }

        if (DEBUG_DB) {
            GlobalTimer.registerTask("db-logger", 5 * SECONDS) {
                CurrentWorld.players.forEach {
                    sendDBDebugMessage(it, it.data.toString())
                }
            }
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val playerId = player.uniqueId

        initPlayerEntryForMemoryDB(playerId)

        player.updateData {
            login.apply {
                count += 1
                lastJoinedAt = Date().toString()
            }
        }

        println("Login state: ${memoryPlayerData[event.player.uniqueId]}")

        syncMemoryPlayerToDB(playerId)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        println("Logout state: ${memoryPlayerData[event.player.uniqueId]}")

        syncMemoryPlayerToDB(event.player.uniqueId)
    }
}

var Player.data: DBPlayer
    get() = memoryPlayerData[this.uniqueId] ?: error("DB data for player ${this.uniqueId} is not bound to player instance!")
    set(data) {
        memoryPlayerData[this.uniqueId] = data
    }

fun Player.updateData(modifier: DBPlayer.() -> Unit) {
    modifyPlayerData(this.uniqueId, modifier)
}

fun modifyPlayerData(playerId: UUID, modifier: DBPlayer.() -> Unit) {
    memoryPlayerData[playerId]?.let(modifier)
}

/**
 * This function populates an entry in the `memoryPlayerData` map. If a user is new
 */
fun initPlayerEntryForMemoryDB(playerId: UUID) {
    val dbPlayer = PitPlayerTable.getItem(DBPlayer(playerId = playerId.toString()))
    if (dbPlayer == null) {
        registerNewPlayerToMemoryDB(playerId)
        return
    }

    memoryPlayerData[playerId] = dbPlayer
}

private fun registerNewPlayerToMemoryDB(playerId: UUID) {
    memoryPlayerData[playerId] = DBPlayer(
        playerId = playerId.toString(),
        login = LoginInformation()
    )
}

private fun syncMemoryPlayerToDB(playerId: UUID) {
    val playerStateInMemory = memoryPlayerData[playerId] ?: return

    try {
        PitPlayerTable.putItem(playerStateInMemory)
    } catch (e: DynamoDbException) {
        println(e.stackTrace)
        println("Could not synchronize player from memory to the database: PartitionKey/PlayerId=$playerId")
    }
}
