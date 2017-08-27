package com.workingbit.share.domain.impl;

import com.workingbit.share.domain.BaseDomain;
import com.workingbit.share.domain.ICoordinates;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Aleksey Popryaduhin on 09:28 10/08/2017.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Draught implements ICoordinates, BaseDomain {
  /**
   * row
   */
  private int v;
  /**
   * col
   */
  private int h;

  private int dim;

  private boolean black;
  private boolean queen;
  private boolean beaten;
  private boolean highlighted;

  private Square holderSquare;

  public Draught(int v, int h, int dim, Square holderSquare) {
    this.v = v;
    this.h = h;
    this.dim = dim;
    this.holderSquare = holderSquare;
  }

  public Draught(int v, int h, int dim, boolean black, Square holderSquare) {
    this(v, h, dim, holderSquare);
    this.black = black;
  }

  public Draught(int v, int h, int dimension, boolean b) {

  }

  public Draught(int v, int h, int dimension) {

  }

  @Override
  public String toString() {
    return "Draught{" +
        "notation=" + toNotation() +
        ", v=" + v +
        ", h=" + h +
        ", black=" + black +
        ", queen=" + queen +
        ", beaten=" + beaten +
        ", highlighted=" + highlighted +
        '}';
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return new Draught(v, h, getDim(), black, queen, beaten, highlighted, holderSquare);
  }
}
