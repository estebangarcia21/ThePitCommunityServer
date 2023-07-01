package org.thepitcommunityserver.db

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import java.util.*


@DynamoDbBean
data class DBPlayer(
    @get:DynamoDbPartitionKey
    var playerId: String = "",

    var xp: Int = 0,
    var gold: Double = 0.0,
    var prestige: Int = 0,
    var level: Int = 1,

    var login: LoginInformation = LoginInformation()
)

@DynamoDbBean
data class LoginInformation(
    var count: Int = 0,
    var firstJoinedAt: String = Date().toString(),
    var lastJoinedAt: String = Date().toString()
)
