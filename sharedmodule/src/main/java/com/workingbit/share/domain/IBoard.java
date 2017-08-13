package com.workingbit.share.domain;

import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.impl.BoardChanger;

/**
 * Created by Aleksey Popryaduhin on 09:02 12/08/2017.
 */
public interface IBoard {

  String getId();

  void setId(String id);

  String getCurrentBoardId();

  void setCurrentBoardId(String boardId);

  boolean isBlack();

  EnumRules getRules();

  int getSquareSize();

}
