package com.workingbit.history.service;

import com.github.rutledgepaulv.prune.Tree;
import com.workingbit.history.domain.impl.BoardHistory;
import com.workingbit.history.domain.impl.BoardTreeNode;
import com.workingbit.share.domain.impl.BoardContainer;

import javax.validation.constraints.NotNull;
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
  }

  public static BoardHistoryManager getInstance() {
    return INSTANCE;
  }

  /**
   * Adds a Changeable to manage.
   *
   * @param changeable
   */
  public Tree.Node<Optional<BoardContainer>> addBoard(@NotNull BoardContainer changeable) {
    return boardHistory.addBoard(changeable);
  }

  /**
   * Undoes the Changeable at the current index.
   *
   * @throws IllegalStateException if canUndo returns false.
   */
  public Optional<BoardContainer> undo() {
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

  public String getHistory() {
    return boardHistory.getJson();
  }

  public BoardTreeNode createFromJson(String json) {
    return boardHistory.fromJson(json);
  }

  public BoardTreeNode getBoardTree() {
    return boardHistory.getBoardTree();
  }
}
