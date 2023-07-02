package org.thepitcommunityserver.db

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException
import java.net.URI
import java.util.*
import kotlin.system.exitProcess

const val PIT_TABLE_NAME = "ThePit"

private val llDynamoClient = DynamoDbClient.builder()
    .endpointOverride(URI("http://localhost:8147"))
    .region(Region.US_WEST_2)
    .credentialsProvider(DefaultCredentialsProvider.create())
    .build()

val dynamoClient: DynamoDbEnhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(llDynamoClient).build()

private val tableSchema: TableSchema<DBPlayer> = BeanTableSchema.create(DBPlayer::class.java)

val PitPlayerTable: DynamoDbTable<DBPlayer>
    get() {
        val table: DynamoDbTable<DBPlayer>

        try {
            table = dynamoClient.table(PIT_TABLE_NAME, tableSchema)
        } catch (e: SdkClientException) {
            println("Error connecting to DynamoDB due to client-side error: ${e.message}")
            println("Did you start the local DynamoDB database? Consult the README.md for setup instructions.")
            exitProcess(1)
        } catch (e: DynamoDbException) {
            println("Error connecting to DynamoDB: ${e.message}")
            exitProcess(1)
        }

        return table
    }
