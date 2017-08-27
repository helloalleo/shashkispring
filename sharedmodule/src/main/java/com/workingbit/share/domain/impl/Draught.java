package com.workingbit.share.domain.impl;

import com.workingbit.share.domain.BaseDomain;
import com.workingbit.share.domain.ICoordinates;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksey Popryaduhin on 09:28 10/08/2017.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"v", "h"})
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
  private List<List<Square>> diagonals = new ArrayList<>();

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

//  public Draught(int v, int h, int dimension, boolean b) {
//
//  }
//
//  public Draught(int v, int h, int dimension) {
//
//  }

  public void addDiagonal(List<Square> diagonal) {
    this.diagonals.add(diagonal);
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
        ", diagonals=" + diagonals +
        '}';
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return new Draught(v, h, getDim(), black, queen, beaten, highlighted, holderSquare, ObjectUtils.clone(diagonals));
  }
}
