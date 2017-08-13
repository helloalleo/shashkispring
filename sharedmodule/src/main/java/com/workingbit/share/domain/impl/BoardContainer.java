package com.workingbit.share.domain.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.share.common.Log;
import com.workingbit.share.domain.IBoardContainer;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksey Popryaduhin on 19:54 12/08/2017.
 */
@Data
public class BoardContainer implements IBoardContainer {

  private String id;
  private List<Square> squares = new ArrayList<>();
  private List<Draught> whiteDraughts = new ArrayList<>();
  private List<Draught> blackDraughts = new ArrayList<>();
  private Draught selectedDraught;

  public BoardContainer() {
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

  public BoardContainer undo() {
    Log.debug(id);
    return this;
  }

  public BoardContainer redo() {
    Log.debug(id);
    return this;
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
