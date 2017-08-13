package com.workingbit.share.domain.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.share.domain.Changeable;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksey Popryaduhin on 19:54 12/08/2017.
 */
public class BoardContainer implements Changeable {

  private List<Square> squares;
  private List<Draught> whiteDraughts;
  private List<Draught> blackDraughts;
  private Draught selectedDraught;

  public BoardContainer() {
    super();
  }

  public BoardContainer(List<Square> squares,
                      List<Draught> whiteDraughts,
                      List<Draught> blackDraughts,
                      Draught selectedDraught) {
    this.squares = ObjectUtils.clone(squares);
    this.whiteDraughts = ObjectUtils.clone(whiteDraughts);
    this.blackDraughts = ObjectUtils.clone(blackDraughts);
    this.selectedDraught = ObjectUtils.clone(selectedDraught);
  }

  public BoardContainer(BoardContainer currentBoard) {
    this(currentBoard.getSquares(),
        currentBoard.getWhiteDraughts(),
        currentBoard.getBlackDraughts(),
        currentBoard.getSelectedDraught());
  }

  @Override
  public BoardContainer undo() {
    return this;
  }

  @Override
  public BoardContainer redo() {
    return this;
  }

  public List<Square> getSquares() {
    return squares;
  }

  public List<Draught> getWhiteDraughts() {
    return whiteDraughts;
  }

  public List<Draught> getBlackDraughts() {
    return blackDraughts;
  }

  public Draught getSelectedDraught() {
    return selectedDraught;
  }

  public void setSelectedDraught(Draught selectedDraught) {
    this.selectedDraught = selectedDraught;
  }

  public void mapBoard(ObjectMapper objectMapper) {
    List<Square> mappedSquares = new ArrayList<>(squares.size());
    for (int i = 0; i < squares.size(); i++) {
      Square square = objectMapper.convertValue(squares.get(i), Square.class);
      mappedSquares.add(square);
    }
    squares = mappedSquares;
  }
}
