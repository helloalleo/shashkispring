package com.workingbit.board.service;

import com.workingbit.board.dao.BoardHistoryDao;
import com.workingbit.share.domain.IBoardContainer;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.BoardHistory;
import com.workingbit.share.domain.impl.BoardHistoryNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Aleksey Popryaduhin on 19:52 12/08/2017.
 */
@Service
public class BoardHistoryManagerService {

  @Autowired
  private BoardHistoryDao boardHistoryDao;
  private BoardHistory boardHistory;

  /**
   * Creates a new ChangeManager object which is initially empty.
   */
  public BoardHistoryManagerService() {
    boardHistory = new BoardHistory();
  }

  /**
   * Creates a new ChangeManager which is a duplicate of the parameter in both contents and current index.
   *
   * @param manager
   */
  public BoardHistoryManagerService(BoardHistoryManagerService manager) {
    this();
    boardHistory.setCurrentBoard(manager.getBoardHistory().getCurrentBoard());
  }

  /**
   * Clears all Changables contained in this manager.
   */
  public void clear() {
    boardHistory.setCurrentBoard(boardHistory.getParentBoard());
  }

  /**
   * Adds a Changeable to manage.
   *
   * @param changeable
   */
  public void addChangeable(IBoardContainer changeable) {
    BoardHistoryNode node = new BoardHistoryNode(changeable);
    boardHistory.getCurrentBoard().setRight(node);
    node.setLeft(boardHistory.getCurrentBoard());
    boardHistory.setCurrentBoard(node);
    boardHistoryDao.save(boardHistory);
  }

  /**
   * Determines if an undo can be performed.
   *
   * @return
   */
  public boolean canUndo() {
    return boardHistory.getCurrentBoard() != boardHistory.getParentBoard();
  }

  /**
   * Determines if a redo can be performed.
   *
   * @return
   */
  public boolean canRedo() {
    return boardHistory.getCurrentBoard().getRight() != null;
  }

  /**
   * Undoes the Changeable at the current index.
   *
   * @throws IllegalStateException if canUndo returns false.
   */
  public IBoardContainer undo() {
    //validate
    if (!canUndo()) {
      throw new IllegalStateException("Cannot undo. Index is out of range.");
    }
    //undo
    IBoardContainer undo = boardHistory.getCurrentBoard().getBoard().undo();
    //set index
    moveLeft();
    return undo;
  }

  /**
   * Moves the internal pointer of the backed linked list to the left.
   *
   * @throws IllegalStateException If the left index is null.
   */
  private void moveLeft() {
    if (boardHistory.getCurrentBoard().getLeft() == null) {
      throw new IllegalStateException("Internal index set to null.");
    }
    boardHistory.setCurrentBoard(boardHistory.getCurrentBoard().getLeft());
  }

  /**
   * Moves the internal pointer of the backed linked list to the right.
   *
   * @throws IllegalStateException If the right index is null.
   */
  private void moveRight() {
    if (boardHistory.getCurrentBoard().getRight() == null) {
      throw new IllegalStateException("Internal index set to null.");
    }
    boardHistory.setCurrentBoard(boardHistory.getCurrentBoard().getRight());
  }

  /**
   * Redoes the Changable at the current index.
   *
   * @throws IllegalStateException if canRedo returns false.
   */
  public BoardContainer redo() {
    //validate
    if (!canRedo()) {
      throw new IllegalStateException("Cannot redo. Index is out of range.");
    }
    //reset index
    moveRight();
    //redo
    return boardHistory.getCurrentBoard().getBoard().redo();
  }

  public BoardHistory getBoardHistory() {
    return boardHistory;
  }
}
