package com.workingbit.share.domain.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.workingbit.board.common.DBConstants;
import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.IBoard;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by Aleksey Popryaduhin on 18:31 09/08/2017.
 */
@DynamoDBTable(tableName = DBConstants.BOARD_TABLE)
@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class Board implements IBoard {

  @DynamoDBAutoGeneratedKey
  @DynamoDBHashKey(attributeName = "id")
  private String id;

  @DynamoDBAttribute(attributeName = "currentBoard")
  private BoardContainer currentBoard;

  /**
   * Is player on the black side?
   */
  @DynamoDBAttribute(attributeName = "black")
  private boolean black;

  @DynamoDBTypeConvertedEnum
  @DynamoDBAttribute(attributeName = "rules")
  private EnumRules rules;

  /**
   * Size of one square
   */
  @DynamoDBAttribute(attributeName = "squareSize")
  private int squareSize;

  public Board(BoardContainer board, boolean black, EnumRules rules, int squareSize) {
    this.currentBoard = board;
    this.black = black;
    this.rules = rules;
    this.squareSize = squareSize;
  }
}
