package org.thepitcommunityserver.db

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
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

    @EventHandler(priority = EventPriority.HIGHEST)
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

        syncMemoryPlayerToDB(playerId)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        syncMemoryPlayerToDB(event.player.uniqueId)
    }
}

var Player.data: DBPlayer
    get() {
        val uuid = this.uniqueId

        var data = memoryPlayerData[uuid]
        if (data == null) {
            data = initPlayerEntryForMemoryDB(uuid)
        }

        return data
    }
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
fun initPlayerEntryForMemoryDB(playerId: UUID): DBPlayer {
    if (memoryPlayerData.containsKey(playerId)) return memoryPlayerData[playerId]!!

    val dbPlayer = PitPlayerTable.getItem(DBPlayer(playerId = playerId.toString()))
        ?: initializePlayerDBBean(playerId)

    memoryPlayerData[playerId] = dbPlayer

    return dbPlayer
}

private fun initializePlayerDBBean(playerId: UUID): DBPlayer {
    return DBPlayer(
        playerId = playerId.toString(),
        login = LoginInformation()
    )
}

private fun syncMemoryPlayerToDB(playerId: UUID) {
    val playerStateInMemory = memoryPlayerData[playerId] ?: return

    try {
        PitPlayerTable.putItem(playerStateInMemory)
        memoryPlayerData.remove(playerId)
    } catch (e: DynamoDbException) {
        println(e.stackTrace)
        println("Could not synchronize player from memory to the database: PartitionKey/PlayerId=$playerId")
    }
}
