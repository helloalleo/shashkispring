package com.workingbit.history.service;

import com.github.rutledgepaulv.prune.Tree;
import com.workingbit.history.domain.impl.BoardHistory;
import com.workingbit.share.domain.IBoardContainer;
import com.workingbit.share.domain.impl.BoardContainer;

import java.util.Optional;

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
//    boardHistory.setLast(boardHistory.getFirst());
  }

  public static BoardHistoryManager getInstance() {
    return INSTANCE;
  }

  /**
   * Clears all Changables contained in this manager.
   */
//  public void clear() {
//    boardHistory.setLast(boardHistory.getFirst());
//  }

  /**
   * Adds a Changeable to manage.
   *
   * @param changeable
   */
  public BoardHistory addBoard(BoardContainer changeable) {
    boardHistory.addBoard(Optional.of(changeable));
    return boardHistory;
  }

  /**
   * Determines if an undo can be performed.
   *
   * @return
   */
  public boolean canUndo() {
    return boardHistory.canUndo();
//    return boardHistory.getLast() != boardHistory.getFirst();
  }

  /**
   * Determines if a redo can be performed.
   *
   * @return
   */
  public boolean canRedo() {
    return boardHistory.canRedo();
//    return boardHistory.getLast().getNext() != null;
  }

  /**
   * Undoes the Changeable at the current index.
   *
   * @throws IllegalStateException if canUndo returns false.
   */
  public Optional<IBoardContainer> undo() {
    //validate
    if (!boardHistory.canUndo()) {
      throw new IllegalStateException("Cannot undo. Index is out of range.");
    }
    //set index
    boardHistory.moveUp();
    //undo
    Optional<BoardContainer> boardContainerOptiona = boardHistory.getLast().getData();
    return boardContainerOptiona
        .map(BoardContainer::undo);
  }

  /**
   * Redoes the Changable at the current index.
   *
   * @throws IllegalStateException if canRedo returns false.
   */
  public Optional<BoardContainer> redo(Tree.Node<Optional<BoardContainer>> branch) {
    //validate
    if (!boardHistory.canRedo(branch)) {
      throw new IllegalStateException("Cannot redo. Index is out of range.");
    }
    //reset index
    boardHistory.moveDown(branch);
    //redo
    Optional<BoardContainer> boardContainerOptional = boardHistory.getLast().getData();
    return boardContainerOptional
        .map(BoardContainer::redo);
  }

  public Optional<BoardContainer> redo() {
    if (!boardHistory.canRedo()) {
      throw new IllegalStateException("Cannot redo. Index is out of range.");
    }
    boardHistory.moveDown();
    Optional<BoardContainer> boardContainerOptional = boardHistory.getLast().getData();
    return boardContainerOptional
        .map(BoardContainer::redo);
  }
}
