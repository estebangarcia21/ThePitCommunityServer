package org.thepitcommunityserver.db

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest

class Sync(private val playerUuid: String) {
    private val dynamoDbClient: DynamoDbClient = DynamoDbClient.builder()
        .region(Region.US_WEST_2)
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build()

    fun sync() {
        val tableName = "ThePitCommunityServer-Main"

        val values = mapOf(
            "UUID" to AttributeValue.builder().s(playerUuid).build()
        )

        val putItemRequest = PutItemRequest.builder()
            .tableName(tableName)
            .item(values)
            .build()

        try {
            dynamoDbClient.putItem(putItemRequest)
            println("Item added successfully")
        } catch (e: DynamoDbException) {
            println("Error adding item: ${e.message}")
        }
    }
}
