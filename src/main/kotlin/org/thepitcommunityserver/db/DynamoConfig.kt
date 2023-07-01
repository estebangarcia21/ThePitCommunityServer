package org.thepitcommunityserver.db

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI
import java.util.*

const val PIT_TABLE_NAME = "ThePit"

private val llDynamoClient = DynamoDbClient.builder()
    .endpointOverride(URI("http://localhost:8147"))
    .region(Region.US_WEST_2)
    .credentialsProvider(DefaultCredentialsProvider.create())
    .build()

val dynamoClient: DynamoDbEnhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(llDynamoClient).build()

private val tableSchema: TableSchema<DBPlayer> = BeanTableSchema.create(DBPlayer::class.java)
val PitPlayerTable: DynamoDbTable<DBPlayer> = dynamoClient.table(PIT_TABLE_NAME, tableSchema)
