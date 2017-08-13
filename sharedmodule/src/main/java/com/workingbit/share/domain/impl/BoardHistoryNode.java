package com.workingbit.share.domain.impl;

import com.workingbit.share.domain.IBoardContainer;
import lombok.Data;

@Data
public class BoardHistoryNode {
  private BoardHistoryNode left;
  private BoardHistoryNode right;
  private IBoardContainer board;

  public BoardHistoryNode(IBoardContainer board) {
    this.board = board;
  }
}