package org.thepitcommunityserver.db

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

const val PIT_TABLE_NAME = "ThePit"

private val llDynamoClient = DynamoDbClient.builder()
    .endpointOverride(URI("http://localhost:8147"))
    .region(Region.US_WEST_2)
    .credentialsProvider(DefaultCredentialsProvider.create())
    .build()

val dynamoClient = DynamoDbEnhancedClient.builder().dynamoDbClient(llDynamoClient).build()

private val tableSchema: TableSchema<Player> = BeanTableSchema.create(Player::class.java)
val PitTable: DynamoDbTable<Player> = dynamoClient.table(PIT_TABLE_NAME, tableSchema)
