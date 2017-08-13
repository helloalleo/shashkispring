package com.workingbit.share.domain;

import com.workingbit.share.common.EnumRules;

/**
 * Created by Aleksey Popryaduhin on 09:02 12/08/2017.
 */
public interface IBoard {

  String getId();

  void setId(String id);

  IBoardContainer getCurrentBoard();

  void setCurrentBoard(IBoardContainer board);

  boolean isBlack();

  EnumRules getRules();

  int getSquareSize();

}
