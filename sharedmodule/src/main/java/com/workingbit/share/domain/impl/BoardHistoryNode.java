package com.workingbit.share.domain.impl;

import com.workingbit.share.domain.IBoardContainer;

public class BoardHistoryNode {
  private BoardHistoryNode left;
  private BoardHistoryNode right;
  private IBoardContainer board;

  public BoardHistoryNode() {
  }

  public BoardHistoryNode(IBoardContainer board) {
    this.board = board;
  }

  public BoardHistoryNode getLeft() {
    return left;
  }

  public void setLeft(BoardHistoryNode left) {
    this.left = left;
  }

  public BoardHistoryNode getRight() {
    return right;
  }

  public void setRight(BoardHistoryNode right) {
    this.right = right;
  }

  public IBoardContainer getBoard() {
    return board;
  }

  public void setBoard(IBoardContainer board) {
    this.board = board;
  }
}