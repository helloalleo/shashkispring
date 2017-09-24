package com.workingbit.share.domain.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.workingbit.share.common.DBConstants;
import com.workingbit.share.common.DraughtMapConverter;
import com.workingbit.share.domain.BaseDomain;
import com.workingbit.share.model.EnumRules;

import java.util.*;

/**
 * Created by Aleksey Popryaduhin on 23:21 21/09/2017.
 */
@DynamoDBTable(tableName = DBConstants.BOARD_TABLE)
public class Board implements BaseDomain {

  private static Integer counter = 1;
  private static Integer counter2 = 1;

  @DynamoDBHashKey(attributeName = "id")
  private String id;

  @DynamoDBRangeKey(attributeName = "createdAt")
  private Date createdAt;

  @JsonIgnore
  @DynamoDBAttribute(attributeName = "boardBoxId")
  private String boardBoxId;

  /**
   * Next boards map. Key next square notation, value board id
   */
  @JsonIgnore
  @DynamoDBTypeConvertedJson(targetType = HashMap.class)
  @DynamoDBAttribute(attributeName = "next")
  private Map<String, String> next = new HashMap<String, String>();

  /**
   * Next boards map. Key next square notation, value board id
   */
  @DynamoDBTypeConvertedJson(targetType = LinkedList.class)
  @DynamoDBAttribute(attributeName = "previousBoards")
  private LinkedList<String> previousBoards = new LinkedList<>();

  @DynamoDBTypeConvertedJson(targetType = LinkedList.class)
  @DynamoDBAttribute(attributeName = "nextBoard")
  private LinkedList<String> nextBoard = new LinkedList<>();

  /**
   * Black draughts associated with owner square
   */
  @JsonIgnore
  @DynamoDBTypeConverted(converter = DraughtMapConverter.class)
  @DynamoDBAttribute(attributeName = "blackDraughts")
  private Map<String, Draught> blackDraughts = new HashMap<>();

  @JsonIgnore
  @DynamoDBTypeConverted(converter = DraughtMapConverter.class)
  @DynamoDBAttribute(attributeName = "whiteDraughts")
  private Map<String, Draught> whiteDraughts = new HashMap<>();

  /**
   * Currently selected square
   */
  @DynamoDBTypeConvertedJson(targetType = Square.class)
  @DynamoDBAttribute(attributeName = "selectedSquare")
  private Square selectedSquare;


  /**
   * Next move for draught
   */
  @DynamoDBTypeConvertedJson(targetType = Square.class)
  @DynamoDBAttribute(attributeName = "nextSquare")
  private Square nextSquare;

  @DynamoDBTypeConvertedJson(targetType = Square.class)
  @DynamoDBAttribute(attributeName = "previousSquare")
  private Square previousSquare;

  /**
   * Squares for API
   */
  @DynamoDBIgnore
  private List<Square> squares = new ArrayList<>();

  /**
   * Squares without nulls
   */
  @DynamoDBIgnore
  @JsonIgnore
  private List<Square> assignedSquares = new ArrayList<>();

  /**
   * Is player on the black side?
   */
  @DynamoDBAttribute(attributeName = "black")
  private boolean black;

  @DynamoDBTypeConvertedEnum
  @DynamoDBAttribute(attributeName = "rules")
  private EnumRules rules;

  /**
   * Current move cursor
   */
  @JsonIgnore
  @DynamoDBAttribute(attributeName = "cursor")
  private boolean cursor;

  public Board() {
  }

  public Board(boolean black, EnumRules rules) {
    this.black = black;
    this.rules = rules;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public boolean isCursor() {
    return cursor;
  }

  public void setCursor(boolean cursor) {
    this.cursor = cursor;
  }

  public String getBoardBoxId() {
    return boardBoxId;
  }

  public void setBoardBoxId(String boardBoxId) {
    this.boardBoxId = boardBoxId;
  }

  public Map<String, String> getNext() {
    return next;
  }

  public void setNext(Map<String, String> next) {
    this.next = next;
  }

  public LinkedList<String> getPreviousBoards() {
    return previousBoards;
  }

  public void setPreviousBoards(LinkedList<String> previousBoards) {
    this.previousBoards = previousBoards;
  }

  public void pushPreviousBoard(String boardId) {
    this.previousBoards.push(boardId);
  }

  public String popPreviousBoard() {
    return previousBoards.isEmpty() ? null : previousBoards.pop();
  }

  public LinkedList<String> getNextBoard() {
    return nextBoard;
  }

  public void setNextBoard(LinkedList<String> nextBoard) {
    this.nextBoard = nextBoard;
  }

  public String popNextBoard() {
    return nextBoard.isEmpty() ? null : nextBoard.pop();
  }

  public void pushNextBoard(String boardId) {
    nextBoard.push(boardId);
  }

  public Map<String, Draught> getBlackDraughts() {
    return blackDraughts;
  }

  public void setBlackDraughts(Map<String, Draught> blackDraughts) {
    this.blackDraughts = blackDraughts;
  }

  public Map<String, Draught> getWhiteDraughts() {
    return whiteDraughts;
  }

  public void setWhiteDraughts(Map<String, Draught> whiteDraughts) {
    this.whiteDraughts = whiteDraughts;
  }

  public Square getSelectedSquare() {
    return selectedSquare;
  }

  public void setSelectedSquare(Square selectedSquare) {
    this.selectedSquare = selectedSquare;
  }

  public Square getNextSquare() {
    return nextSquare;
  }

  public void setNextSquare(Square nextSquare) {
    this.nextSquare = nextSquare;
  }

  public List<Square> getSquares() {
    return squares;
  }

  public void setSquares(List<Square> squares) {
    this.squares = squares;
  }

  public List<Square> getAssignedSquares() {
    return assignedSquares;
  }

  public void setAssignedSquares(List<Square> assignedSquares) {
    this.assignedSquares = assignedSquares;
  }

  public boolean isBlack() {
    return black;
  }

  public void setBlack(boolean black) {
    this.black = black;
  }

  public EnumRules getRules() {
    return rules;
  }

  public void setRules(EnumRules rules) {
    this.rules = rules;
  }

  public void addBlackDraughts(String notation, Draught draught) {
    blackDraughts.put(notation, draught);
  }

  public void addWhiteDraughts(String notation, Draught draught) {
    whiteDraughts.put(notation, draught);
  }

  public void setPreviousSquare(Square previousSquare) {
    this.previousSquare = previousSquare;
  }

  public Square getPreviousSquare() {
    return previousSquare;
  }
}
