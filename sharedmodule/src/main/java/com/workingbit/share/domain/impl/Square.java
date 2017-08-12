package com.workingbit.share.domain.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.workingbit.share.domain.ICoordinates;
import com.workingbit.share.domain.ISquare;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by Aleksey Popryaduhin on 09:26 10/08/2017.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"v", "h"})
public class Square implements ICoordinates, ISquare {

  /**
   * row
   */
  private int v;
  /**
   * col
   */
  private int h;
  /**
   * on the main part where we have the draughts
   */
  private boolean main;
  /**
   * if square highlighted for allowing to move
   */
  private boolean highlighted;
  private int size;

  private Draught draught;

  /**
   * Selected draught is point for new recursion
   */
  @JsonIgnore
  private Draught pointDraught;

  public Square(int v, int h, boolean main, int size, Draught draught) {
    this.v = v;
    this.h = h;
    this.main = main;
    this.size = size;
    this.draught = draught;
  }

  @JsonIgnore
  public boolean isOccupied() {
    return draught != null;
  }
}
