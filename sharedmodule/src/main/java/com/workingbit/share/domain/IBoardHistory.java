package com.workingbit.share.domain;

import com.workingbit.share.domain.impl.BoardHistoryNode;

/**
 * Created by Aleksey Popryaduhin on 10:27 13/08/2017.
 */
public interface IBoardHistory {

  BoardHistoryNode getCurrentBoard();
}
