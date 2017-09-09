package com.workingbit.share.domain.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksey Popryaduhin on 17:34 09/09/2017.
 */
@Data
@AllArgsConstructor
public class BoardSnapshot implements Cloneable {

  private String id;

  private List<Draught> whiteDraughts = new ArrayList<>();
  private List<Draught> blackDraughts = new ArrayList<>();

  /**
   * Currently selected square
   */
  private Square selectedSquare;

  /**
   * Moved forward position
   */
  private Square forwardSquare;

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return new BoardSnapshot(id,
        ObjectUtils.clone(whiteDraughts),
        ObjectUtils.clone(blackDraughts),
        ObjectUtils.clone(selectedSquare),
        ObjectUtils.clone(forwardSquare));
  }
}
