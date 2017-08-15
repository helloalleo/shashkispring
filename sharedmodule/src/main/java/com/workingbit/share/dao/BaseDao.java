package com.workingbit.share.dao;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static com.workingbit.share.common.Utils.isBlank;

/**
 * Created by Aleksey Popryaduhin on 18:56 09/08/2017.
 */
public class BaseDao<T> {

  private final Class<T> clazz;
  private final DynamoDBMapper dynamoDBMapper;
  private final ObjectMapper mapper;
  private final boolean test;
  private String dbDir = "~/dbDir";

  protected BaseDao(Class<T> clazz, String region, String endpoint, boolean test) {
    this.clazz = clazz;

    AmazonDynamoDB ddb;
    if (test) {
      ddb = AmazonDynamoDBClientBuilder.standard()
          .withEndpointConfiguration(
              new AwsClientBuilder.EndpointConfiguration(endpoint, region))
          .build();
    } else {
      ddb = AmazonDynamoDBClientBuilder
          .standard()
          .withRegion(region)
          .build();
    }
    this.test = test;
    dynamoDBMapper = new DynamoDBMapper(ddb);
    this.mapper = new ObjectMapper();
  }

  protected DynamoDBMapper getDynamoDBMapper() {
    return dynamoDBMapper;
  }

  public void save(final T entity) {
    dynamoDBMapper.save(entity);
  }

  public PaginatedScanList<T> findAll() {
    DynamoDBScanExpression dynamoDBQueryExpression = new DynamoDBScanExpression();
    return dynamoDBMapper.scan(clazz, dynamoDBQueryExpression);
  }

  public Optional<T> findById(String entityId) {
    if (isBlank(entityId)) {
      return Optional.empty();
    }
    T entity = dynamoDBMapper.load(clazz, entityId);
    if (entity != null) {
      return Optional.of(entity);
    }
    return Optional.empty();
  }

  public void delete(final String entityId) {
    if (isBlank(entityId)) {
      return;
    }
    findById(entityId)
        .ifPresent(dynamoDBMapper::delete);
  }
}
