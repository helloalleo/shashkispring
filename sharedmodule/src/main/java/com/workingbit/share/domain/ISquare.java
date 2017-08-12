package com.workingbit.share.domain;

import com.workingbit.share.domain.impl.Draught;

/**
 * Created by Aleksey Popryaduhin on 09:03 12/08/2017.
 */
public interface ISquare {

  int getV();

  int getH();

  boolean isMain();

  boolean isHighlighted();

  int getSize();

  Draught getDraught();

  void setDraught(Draught draught);

  Draught getPointDraught();

  boolean isOccupied();

  void setPointDraught(Draught draught);

}
