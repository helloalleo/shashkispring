package com.workingbit.share.common;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.share.model.DraughtMap;

import java.io.IOException;

/**
 * Created by Aleksey Popryaduhin on 16:45 22/09/2017.
 */
public class DraughtMapConverter implements DynamoDBTypeConverter<String, DraughtMap> {

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public String convert(DraughtMap object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      Log.error(e.getMessage());
      return "";
    }
  }

  @Override
  public DraughtMap unconvert(String object) {
    try {
      return mapper.readValue(object, DraughtMap.class);
    } catch (IOException e) {
      Log.error(e.getMessage());
      return null;
    }
  }
}
