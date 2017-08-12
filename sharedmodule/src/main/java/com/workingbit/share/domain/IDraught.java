package com.workingbit.share.domain;

/**
 * Created by Aleksey Popryaduhin on 09:03 12/08/2017.
 */
public interface IDraught {

  int getV();

  void setV(int v);

  int getH();

  void setH(int h);

  boolean isBlack();

  boolean isQueen();

  boolean isBeaten();

  void setBeaten(boolean beaten);

  boolean isHighlighted();

  void setHighlighted(boolean highlighted);

}
