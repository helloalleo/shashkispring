package com.workingbit.history.domain;

/**
 * Created by Aleksey Popryaduhin on 20:50 13/08/2017.
 */
public interface IBoardTree {

  String getId();

  void setId(String id);

  String getBoardId();

  void setBoardId(String id);

  String getHistory();

  void setHistory(String history);
}
