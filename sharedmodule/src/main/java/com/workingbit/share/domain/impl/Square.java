package com.workingbit.share.domain.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.workingbit.share.domain.BaseDomain;
import com.workingbit.share.domain.ICoordinates;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksey Popryaduhin on 09:26 10/08/2017.
 */
@Data
@AllArgsConstructor
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
   * Board's dimension
   */
  @JsonIgnore
  private int dim;

  /**
   * on the main part where we have the draughts
   */
  private boolean main;

  /**
   * if square highlighted for allowing to move
   */
  private boolean highlighted;

  private Draught draught;

  @JsonIgnore
  private List<List<Square>> diagonals = new ArrayList<>();

  public Square(int v, int h, int dim, boolean main, Draught draught) {
    this.v = v;
    this.h = h;
    this.dim = dim;
    this.main = main;
    this.draught = draught;
  }

  public Square(int v, int h, int dim, boolean main) {
    this(v, h, dim, main, null);
  }

  public boolean isOccupied() {
    return draught != null;
  }

  @Override
  public String toString() {
    return "Square{" +
        "notation=" + toNotation() +
        ", highlighted=" + highlighted +
        ", draught=" + draught +
        '}';
  }

  public void addDiagonal(List<Square> diagonal) {
    this.diagonals.add(diagonal);
  }
}