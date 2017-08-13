package com.workingbit.share.domain;

import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.impl.BoardContainer;

/**
 * Created by Aleksey Popryaduhin on 09:02 12/08/2017.
 */
public interface IBoard {

  String getId();

  void setId(String id);

  BoardContainer getCurrentBoard();

  void setCurrentBoard(BoardContainer board);

  boolean isBlack();

  EnumRules getRules();

  int getSquareSize();

}
