package com.workingbit.share.domain.impl;

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
public class Draught implements ICoordinates {
  /**
   * row
   */
  private int v;
  /**
   * col
   */
  private int h;
  private boolean black;
  private boolean queen;
  private boolean beaten;
  private boolean highlighted;

  public Draught(int v, int h) {
    this.v = v;
    this.h = h;
  }

  public Draught(int v, int h, boolean black) {
    this.v = v;
    this.h = h;
    this.black = black;
  }
}
