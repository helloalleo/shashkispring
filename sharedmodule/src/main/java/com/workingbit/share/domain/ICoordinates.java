package com.workingbit.share.domain;

import com.workingbit.share.common.Utils;

/**
 * Created by Aleksey Popryaduhin on 15:10 11/08/2017.
 */
public interface ICoordinates {

  /**
   * row
   */
  int getV();

  void setV(int v);

  /**
   * col
   */
  int getH();

  void setH(int h);

  /**
   * Board's dimension
   */
  int getDim();

  void setDim(int dim);

  default String toNotation() {
    return Utils.alph.get(getH()) + (getDim() - getV());
  }

  default void fromNotation(String pos) {
    setH(Utils.alph.indexOf(String.valueOf(pos.charAt(0))));
    setV(getDim() - Integer.valueOf(String.valueOf(pos.charAt(1))));
  }
}
