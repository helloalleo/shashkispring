package com.workingbit.history.domain;

import com.workingbit.history.domain.impl.BoardTreeNode;

/**
 * Created by Aleksey Popryaduhin on 20:50 13/08/2017.
 */
public interface IBoardHistory {

  String getId();

  void setId(String id);

  String getBoardId();

  void setBoardId(String id);

  BoardTreeNode getRoot();

  void setRoot(BoardTreeNode root);

  BoardTreeNode getCurrent();

  void setCurrent(BoardTreeNode root);
}
