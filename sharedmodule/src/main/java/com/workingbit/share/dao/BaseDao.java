package com.workingbit.share.dao;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.share.common.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.workingbit.share.common.Utils.isBlank;

/**
 * Created by Aleksey Popryaduhin on 18:56 09/08/2017.
 */
public class BaseDao<T, I> {

  private final Class<T> clazz;
  private final Class<I> iclazz;
  private final DynamoDBMapper dynamoDBMapper;
  private final ObjectMapper mapper;
  private final boolean test;
  private String dbDir = "~/dbDir";

  protected BaseDao(Class<T> clazz, Class<I> iclazz, String region, String endpoint, boolean test) {
    this.clazz = clazz;
    this.iclazz = iclazz;

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

  public void save(final I entity) {
    if (test) {
      try {
        Method setId = entity.getClass().getMethod("setId", String.class);
        setId.invoke(entity, UUID.randomUUID().toString());
        if (!Files.exists(Paths.get(dbDir))) {
          Files.createDirectory(Paths.get(dbDir));
        }
        Files.write(Paths.get(dbDir + "/" + entity.getClass().getSimpleName()), mapper.writeValueAsBytes(entity), StandardOpenOption.APPEND);
      } catch (IOException e) {
        Log.error(e.getMessage());
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
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
