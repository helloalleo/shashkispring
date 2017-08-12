package com.workingbit.share.domain;

import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.impl.BoardChanger;
import com.workingbit.share.domain.impl.Draught;

import java.util.List;

/**
 * Created by Aleksey Popryaduhin on 09:02 12/08/2017.
 */
public interface IBoard {

  String getId();

  void setId(String id);

  BoardChanger getSquares();

  void setSquares(BoardChanger squares);

  boolean isBlack();

  EnumRules getRules();

  int getSquareSize();

}
