package com.workingbit.share.domain.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.workingbit.share.domain.BaseDomain;
import com.workingbit.share.domain.ICoordinates;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

  @JsonIgnore
  private int dim;

  private boolean black;
  private boolean queen;
  private boolean beaten;
  private boolean highlighted;

  public Draught(int v, int h, int dim) {
    this.v = v;
    this.h = h;
    this.dim = dim;
  }

  public Draught(int v, int h, int dim, boolean black) {
    this(v, h, dim);
    this.black = black;
  }

  public Draught(int v, int h, int dim, boolean black, boolean queen) {
    this(v, h, dim, black);
    this.queen = queen;
  }

//  public Draught(int v, int h, int dimension, boolean b) {
//
//  }
//
//  public Draught(int v, int h, int dimension) {
//
//  }

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
}
