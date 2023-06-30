package org.thepitcommunityserver.db

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException
import java.util.*

@DynamoDbBean
data class Player(
    // Partition key.
    @get:DynamoDbPartitionKey
    var playerId: String? = null,

    // Fields.
    var loginCount: Double? = null,
    var xp: Int? = null,
    var gold: Int? = null
)

fun sync(playerId: UUID) {
    try {
        PitTable.putItem(
            Player(
                playerId = playerId.toString(),
                loginCount = 1.0,
                xp = 100,
                gold = 100000
            )
        )
        println("Item added successfully")
    } catch (e: DynamoDbException) {
        println("Error adding item: ${e.message}")
    }
}
