package com.workingbit.history.service;

import com.workingbit.history.domain.impl.BoardHistory;
import com.workingbit.share.domain.IBoardContainer;
import com.workingbit.share.domain.impl.BoardContainer;

/**
 * Created by Aleksey Popryaduhin on 19:52 12/08/2017.
 */
public class BoardHistoryManager {

  private static BoardHistoryManager INSTANCE = new BoardHistoryManager();
  private BoardHistory boardHistory;

  /**
   * Creates a new ChangeManager object which is initially empty.
   */
  public BoardHistoryManager() {
    boardHistory = new BoardHistory();
    boardHistory.setLast(boardHistory.getFirst());
  }

  public static BoardHistoryManager getInstance() {
    return INSTANCE;
  }

  /**
   * Clears all Changables contained in this manager.
   */
  public void clear() {
    boardHistory.setLast(boardHistory.getFirst());
  }

  /**
   * Adds a Changeable to manage.
   *
   * @param changeable
   */
  public BoardHistory addChangeable(BoardContainer changeable) {
    boardHistory.addLast(changeable);
    return boardHistory;
  }

  /**
   * Determines if an undo can be performed.
   *
   * @return
   */
  public boolean canUndo() {
    return boardHistory.getLast() != boardHistory.getFirst();
  }

  /**
   * Determines if a redo can be performed.
   *
   * @return
   */
  public boolean canRedo() {
    return boardHistory.getLast().getNext() != null;
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
    IBoardContainer undo = boardHistory.getLast().getBoard().undo();
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
    if (boardHistory.getLast().getPrev() == null) {
      throw new IllegalStateException("Internal index set to null.");
    }
    boardHistory.setLast(boardHistory.getLast().getPrev());
  }

  /**
   * Moves the internal pointer of the backed linked list to the right.
   *
   * @throws IllegalStateException If the right index is null.
   */
  private void moveRight() {
    if (boardHistory.getLast().getNext() == null) {
      throw new IllegalStateException("Internal index set to null.");
    }
    boardHistory.setLast(boardHistory.getLast().getNext());
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
    return boardHistory.getLast().getBoard().redo();
  }

  public BoardHistory getBoardHistory() {
    return boardHistory;
  }
}
