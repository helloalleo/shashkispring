package com.workingbit.share.domain.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.workingbit.share.domain.BaseDomain;
import com.workingbit.share.domain.ICoordinates;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Created by Aleksey Popryaduhin on 09:26 10/08/2017.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"v", "h"})
public class Square implements ICoordinates, BaseDomain {

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

  public boolean isOccupied() {
    return draught != null;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return new Square(v, h, main, size, ObjectUtils.clone(draught));
  }
}
