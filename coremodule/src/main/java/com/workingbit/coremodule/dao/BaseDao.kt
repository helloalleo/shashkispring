package com.workingbit.coremodule.dao

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList

/**
 * Created by Aleksey Popryaduhin on 17:35 23/08/2017.
 */
open class BaseDao<T>(private val clazz: Class<T>, region: String, endpoint: String, test: Boolean) {

    private var dynamoDBMapper: DynamoDBMapper

    init {
        val ddb: AmazonDynamoDB = if (test) {
            AmazonDynamoDBClientBuilder
                    .standard()
                    .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
                    .build()
        } else {
            AmazonDynamoDBClientBuilder
                    .standard()
                    .withRegion(region)
                    .build()
        }
        dynamoDBMapper = DynamoDBMapper(ddb)
    }

    fun save(entity: T) = dynamoDBMapper.save(entity)

    fun delete(entityId: String) = dynamoDBMapper.delete(entityId)

    fun findAll(): PaginatedScanList<T> = dynamoDBMapper.scan(clazz, DynamoDBScanExpression())

    fun findById(entityId: String): T? {
        if (entityId.isBlank()) {
            return null
        }
        return dynamoDBMapper.load(clazz, entityId)
    }

    protected fun getDynamoDBMapper() = dynamoDBMapper
}