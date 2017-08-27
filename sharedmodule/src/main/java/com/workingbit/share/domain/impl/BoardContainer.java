package com.workingbit.share.domain.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.workingbit.share.common.Log;
import com.workingbit.share.domain.BaseDomain;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Aleksey Popryaduhin on 19:54 12/08/2017.
 */
@Data
public class BoardContainer implements BaseDomain {

  private String id;

  private List<Draught> whiteDraughts = new ArrayList<>();
  private List<Draught> blackDraughts = new ArrayList<>();
  private Square selectedSquare;
  @JsonIgnore
  private List<List<Square>> diagonals = new ArrayList<>();
  private List<Square> squares = new ArrayList<>();

  public BoardContainer() {
  }

  public BoardContainer(List<Draught> whiteDraughts,
                        List<Draught> blackDraughts,
                        Square selectedSquare) {
    this.whiteDraughts = ObjectUtils.clone(whiteDraughts);
    this.blackDraughts = ObjectUtils.clone(blackDraughts);
    this.selectedSquare = ObjectUtils.clone(selectedSquare);
  }

  public BoardContainer(BoardContainer currentBoard) {
    this(currentBoard.getWhiteDraughts(),
        currentBoard.getBlackDraughts(),
        currentBoard.getSelectedSquare());
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
    return new BoardContainer(ObjectUtils.clone(whiteDraughts),
        ObjectUtils.clone(blackDraughts),
        ObjectUtils.clone(selectedSquare));
  }

  public void setDiagonals(List<List<Square>> diagonals) {
    this.diagonals = diagonals;
  }
}
