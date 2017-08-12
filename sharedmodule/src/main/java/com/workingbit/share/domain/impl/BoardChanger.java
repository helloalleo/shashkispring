package com.workingbit.share.domain.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.share.domain.Changeable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksey Popryaduhin on 19:54 12/08/2017.
 */
public class BoardChanger implements Changeable {

  private List<Square> squares;
  private List<Draught> whiteDraughts;
  private List<Draught> blackDraughts;
  private Draught selectedDraught;

  public BoardChanger() {
    super();
  }

  public BoardChanger(List<Square> squares,
                      List<Draught> whiteDraughts,
                      List<Draught> blackDraughts,
                      Draught selectedDraught) {
    super();
    this.squares = squares;
    this.whiteDraughts = whiteDraughts;
    this.blackDraughts = blackDraughts;
    this.selectedDraught = selectedDraught;
  }

  @Override
  public BoardChanger undo() {
    return this;
  }

  @Override
  public BoardChanger redo() {
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
