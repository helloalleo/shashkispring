package com.workingbit.history.converter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.history.domain.impl.BoardHistoryNode;

/**
 * Created by Aleksey Popryaduhin on 12:21 13/08/2017.
 */
public class BoardHistoryNodeConverter implements DynamoDBTypeConverter<String, BoardHistoryNode> {

  private ObjectMapper mapper = new ObjectMapper();

  @Override
  public String convert(BoardHistoryNode object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Unable to convert BoardHistoryNode");
    }
  }

  @Override
  public BoardHistoryNode unconvert(String object) {
    return mapper.convertValue(object, BoardHistoryNode.class);
  }
}
