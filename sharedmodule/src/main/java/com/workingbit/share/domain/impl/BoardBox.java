package com.workingbit.share.domain.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.workingbit.share.common.DBConstants;
import com.workingbit.share.domain.BaseDomain;
import com.workingbit.share.model.EnumRules;

import java.util.*;

/**
 * Created by Aleksey Popryaduhin on 19:54 12/08/2017.
 */
@DynamoDBTable(tableName = DBConstants.BOARD_BOX_TABLE)
public class BoardBox implements BaseDomain {

  @DynamoDBHashKey(attributeName = "id")
  private String id;

  @DynamoDBRangeKey(attributeName = "createdAt")
  private Date createdAt;

  @DynamoDBAttribute(attributeName = "articleId")
  private String articleId;

  @JsonIgnore
  @DynamoDBAttribute(attributeName = "boardId")
  private String boardId;

  @DynamoDBIgnore
  private Board board;

  public BoardBox() {
  }

  public BoardBox(Board board) {
    this.board = board;
    this.boardId = board.getId();
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

  public String getArticleId() {
    return articleId;
  }

  public void setArticleId(String articleId) {
    this.articleId = articleId;
  }

  public String getBoardId() {
    return boardId;
  }

  public void setBoardId(String boardId) {
    this.boardId = boardId;
  }

  /**
   * For backward compatibility
   */
  @DynamoDBIgnore
  public Board getBoard() {
    return board;
  }

  public void setBoard(Board board) {
    this.board = board;
  }

  @JsonIgnore
  @DynamoDBIgnore
  public Map<String, Draught> getBlackDraughts() {
    return board.getBlackDraughts();
  }

  public void setBlackDraughts(HashMap<String, Draught> blackDraughts) {
    board.setBlackDraughts(blackDraughts);
  }

  @JsonIgnore
  @DynamoDBIgnore
  public Map<String, Draught> getWhiteDraughts() {
    return board.getWhiteDraughts();
  }

  public void setWhiteDraughts(Map<String, Draught> whiteDraughts) {
    board.setWhiteDraughts(whiteDraughts);
  }

  @JsonIgnore
  @DynamoDBIgnore
  public Square getSelectedSquare() {
    return board.getSelectedSquare();
  }

  public void setSelectedSquare(Square selectedSquare) {
    board.setSelectedSquare(selectedSquare);
  }

  @JsonIgnore
  @DynamoDBIgnore
  public Square getNextSquare() {
    return board.getNextSquare();
  }

  public void setNextSquare(Square nextSquare) {
    board.setNextSquare(nextSquare);
  }

  @JsonIgnore
  @DynamoDBIgnore
  public Square getPreviousSquare() {
    return board.getPreviousSquare();
  }

  public void setPreviousSquare(Square previousSquare) {
    board.setPreviousSquare(previousSquare);
  }

  @JsonIgnore
  @DynamoDBIgnore
  public List<Square> getSquares() {
    return board.getSquares();
  }

  public void setSquares(List<Square> squares) {
    board.setSquares(squares);
  }

  @JsonIgnore
  @DynamoDBIgnore
  public List<Square> getAssignedSquares() {
    return board.getAssignedSquares();
  }

  public void setAssignedSquares(List<Square> assignedSquares) {
    board.setAssignedSquares(assignedSquares);
  }

  @JsonIgnore
  @DynamoDBIgnore
  public boolean isBlack() {
    return board.isBlack();
  }

  public void setBlack(boolean black) {
    board.setBlack(black);
  }

  @JsonIgnore
  @DynamoDBIgnore
  public EnumRules getRules() {
    return board.getRules();
  }

  public void setRules(EnumRules rules) {
    board.setRules(rules);
  }

  /**
   * END For backward compatibility
   */

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BoardBox that = (BoardBox) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(articleId, that.articleId) &&
        Objects.equals(boardId, that.boardId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, articleId, boardId);
  }
}
