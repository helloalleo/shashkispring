package com.workingbit.history.domain;

import com.workingbit.history.domain.impl.BoardHistoryNode;

import java.io.Serializable;

/**
 * Created by Aleksey Popryaduhin on 10:27 13/08/2017.
 */
public interface IBoardHistory extends Serializable {

  BoardHistoryNode getLast();
}
