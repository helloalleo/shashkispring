package com.workingbit.history.domain.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.workingbit.board.common.DBConstants;
import com.workingbit.history.domain.IBoardTree;
import lombok.Data;

/**
 * Created by Aleksey Popryaduhin on 20:51 13/08/2017.
 */
@Data
@DynamoDBTable(tableName = DBConstants.BOARD_HISTORY_TABLE)
public class BoardTree implements IBoardTree {
  @DynamoDBAttribute(attributeName = "history")
  private String history;
}
