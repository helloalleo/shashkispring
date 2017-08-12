package com.workingbit.share.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;

import java.util.List;
import java.util.Optional;

import static com.workingbit.share.common.Utils.isBlank;

/**
 * Created by Aleksey Popryaduhin on 18:56 09/08/2017.
 */
public class BaseDao<T, I> {

  private final Class<T> clazz;
  private final Class<I> iclazz;
  private final DynamoDBMapper dynamoDBMapper;

  protected BaseDao(Class<T> clazz, Class<I> iclazz, String region) {
    this.clazz = clazz;
    this.iclazz = iclazz;
    AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder
        .standard()
        .withRegion(region)
        .build();
    dynamoDBMapper = new DynamoDBMapper(ddb);
  }

  protected DynamoDBMapper getDynamoDBMapper() {
    return dynamoDBMapper;
  }

  public void save(final I entity) {
    dynamoDBMapper.save(entity);
  }

  public List<I> findAll() {
    DynamoDBScanExpression dynamoDBQueryExpression = new DynamoDBScanExpression();
    PaginatedScanList<I> scan = ((PaginatedScanList<I>) dynamoDBMapper.scan(clazz, dynamoDBQueryExpression));
    scan.loadAllResults();
    return scan;
  }

  public Optional<I> findById(String entityId) {
    if (isBlank(entityId)) {
      return Optional.empty();
    }
    T entity = dynamoDBMapper.load(clazz, entityId);
    if (entity != null) {
      return Optional.of(iclazz.cast(entity));
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
