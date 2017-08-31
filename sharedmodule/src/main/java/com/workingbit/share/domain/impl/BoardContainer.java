package com.workingbit.share.domain.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.workingbit.share.common.EnumRules;
import com.workingbit.share.common.Log;
import com.workingbit.share.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;

/**
 * Created by Aleksey Popryaduhin on 19:54 12/08/2017.
 */
@Data
@AllArgsConstructor
public class BoardContainer implements BaseDomain {

  private String id;

  private List<Draught> whiteDraughts = new ArrayList<>();
  private List<Draught> blackDraughts = new ArrayList<>();
  private Square selectedSquare;
  @JsonIgnore
  private List<List<Square>> diagonals = new ArrayList<>();
  private List<Square> squares = new ArrayList<>();
  /**
   * Squares without nulls
   */
  @JsonIgnore
  private List<Square> boardSquares = new ArrayList<>();

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

  public BoardContainer() {
  }

  public BoardContainer(boolean black, EnumRules rules, int squareSize,
                        List<Draught> whiteDraughts,
                        List<Draught> blackDraughts,
                        Square selectedSquare) {
    this.black = black;
    this.rules = rules;
    this.squareSize = squareSize;
    this.whiteDraughts = ObjectUtils.clone(whiteDraughts);
    this.blackDraughts = ObjectUtils.clone(blackDraughts);
    this.selectedSquare = ObjectUtils.clone(selectedSquare);
  }

  public BoardContainer undo() {
    Log.debug(id);
    return this;
  }

  public BoardContainer redo() {
    Log.debug(id);
    return this;
  }

//   public void mapBoard(ObjectMapper objectMapper) {
//    Square[] mappedSquares = new Square[squares.length];
//    for (int i = 0; i < squares.size(); i++) {
//      Square square = objectMapper.convertValue(squares.get(i), Square.class);
//      mappedSquares.add(square);
//    }
//    squares = mappedSquares;
//  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
//    if (!super.equals(o)) return false;
    BoardContainer that = (BoardContainer) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id);
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return new BoardContainer(id,
        ObjectUtils.clone(whiteDraughts),
        ObjectUtils.clone(blackDraughts),
        ObjectUtils.clone(selectedSquare),
        diagonals,
        squares,
        boardSquares,
        black,
        rules,
        squareSize);
  }
}
