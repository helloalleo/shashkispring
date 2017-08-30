package com.workingbit.share.domain.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
  private int size;

  private Draught draught;

  @JsonIgnore
  private List<List<Square>> diagonals = new ArrayList<>();

  public Square(int v, int h, int dim, boolean main, int size, Draught draught) {
    this.v = v;
    this.h = h;
    this.dim = dim;
    this.main = main;
    this.size = size;
    this.draught = draught;
  }

  public Square(int v, int h, int dim, boolean prime, int squareSize) {
    this(v, h, dim, prime, squareSize, null);
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

  @Override
  public Object clone() throws CloneNotSupportedException {
    return new Square(v, h, getDim(), main, highlighted, size, ObjectUtils.clone(draught), ObjectUtils.clone(diagonals));
  }

  public void addDiagonal(List<Square> diagonal) {
    this.diagonals.add(diagonal);
  }
}