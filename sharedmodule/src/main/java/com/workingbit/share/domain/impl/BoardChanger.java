package com.workingbit.share.domain.impl;

import com.workingbit.share.domain.Changeable;
import com.workingbit.share.domain.ISquare;

import java.util.List;

/**
 * Created by Aleksey Popryaduhin on 19:54 12/08/2017.
 */
public class BoardChanger implements Changeable {

  private final List<ISquare> board;
  private final List<Draught> whiteDraughts;
  private final List<Draught> blackDraughts;
  private final Draught selectedDraught;

  public BoardChanger(List<ISquare> board,
                      List<Draught> whiteDraughts,
                      List<Draught> blackDraughts,
                      Draught selectedDraught) {
    super();
    this.board = board;
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

  public List<ISquare> getBoard() {
    return board;
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
}
