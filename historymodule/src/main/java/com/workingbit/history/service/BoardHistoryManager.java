package com.workingbit.history.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.history.domain.impl.BoardHistory;
import com.workingbit.history.domain.impl.BoardTreeNode;
import com.workingbit.share.common.Log;
import com.workingbit.share.domain.impl.BoardContainer;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Created by Aleksey Popryaduhin on 19:52 12/08/2017.
 */
public class BoardHistoryManager {

  private final BoardHistory boardHistory;
    private ObjectMapper mapper = new ObjectMapper();
  private BoardTreeNode current = new BoardTreeNode(null);

//  public void setBoardTree(BoardHistory boardHistory) {
//  }


  public BoardHistoryManager(String boardId) {
    boardHistory = new BoardHistory(boardId);
  }

  public BoardHistoryManager(BoardHistory boardHistory) {
    this.boardHistory = boardHistory;
    current = boardHistory.getCurrent();
  }

  public BoardHistory getBoardHistory() {
    boardHistory.setRoot(getCurrent().getRootOfTree());
    boardHistory.setCurrent(getCurrent());
    try {
      Log.debug("Board History " + mapper.writeValueAsString(boardHistory));
    } catch (JsonProcessingException e) {
      Log.error(e.getMessage());
    }
    return boardHistory;
  }

  /**
   * Adds a Changeable to manage.
   *
   * @param boardContainer
   */
  public BoardTreeNode addBoard(@NotNull BoardContainer boardContainer) {
    BoardTreeNode child = new BoardTreeNode(boardContainer);
    current.addChild(child);
    current = child;
    return current;
  }

  private void moveUp() {
    current = current.getParent();
  }

  private void moveDown(BoardTreeNode branch) {
    current = branch;
  }

  private void moveDown() {
    current = current.getChildren().get(0);
  }

  private boolean canUndo() {
    return current.getParent()
        .getData() != null;
  }

  private boolean canRedo() {
    return !current.getChildren().isEmpty();
  }

  private boolean canRedo(BoardTreeNode branch) {
    return current.getChildren().contains(branch);
  }

  private BoardTreeNode getCurrent() {
    return current;
  }

//  private BoardTreeNode getBoardTreeNodeFromJson(String json) {
//    Log.debug("Read object from json " + json);
//    try {
//      return mapper.readValue(json, BoardTreeNode.class);
//    } catch (IOException e) {
//      return null;
//    }
//  }

//  BoardTreeNode getBoardTreeNode() {
//    return current.getRootOfTree();
//  }

//  private Tree<Tree.Node<BoardContainer>> getTree(Tree.Node<Optional<BoardContainer>> node) {
//    return node.asTree()
//        .mapAsNodes(optionalNode -> Tree.node(optionalNode.getData().orElse(null)))
//        .deepClone(boardContainerNode -> boardContainerNode);
//  }

  public String serializeToJsonBoardTreeNode() {
    try {
      BoardTreeNode boardTree = getCurrent();
      return mapper.writeValueAsString(boardTree);
    } catch (JsonProcessingException e) {
      return "";
    }
  }

  /**
   * Undoes the Changeable at the current index.
   *
   * @throws IllegalStateException if canUndo returns false.
   */
  public Optional<BoardContainer> undo() {
    //validate
    if (!canUndo()) {
      return Optional.empty();
    }
    //set index
    moveUp();
    //undo
    BoardContainer boardContainerOptional = getCurrent().getData();
    boardContainerOptional.undo();
    return Optional.of(boardContainerOptional);
  }

  /**
   * Redoes the Changable at the current index.
   *
   * @throws IllegalStateException if canRedo returns false.
   */
  public Optional<BoardContainer> redo(BoardTreeNode branch) {
    //validate
    if (!canRedo(branch)) {
      return Optional.empty();
    }
    //reset index
    moveDown(branch);
    //redo
    BoardContainer boardContainer = getCurrent().getData();
    boardContainer.redo();
    return Optional.of(boardContainer);
  }

  public Optional<BoardContainer> redo() {
    if (!canRedo()) {
      return Optional.empty();
    }
    moveDown();
    BoardContainer boardContainer = getCurrent().getData();
    boardContainer.redo();
    return Optional.of(boardContainer);
  }

//  public BoardHistory getHistoryByBoardId(String id) {
//    BoardHistory boardHistory = new BoardHistory();
//    boardHistory.setHistory(history);
//    boardHistory.setBoardId(id);
//    return boardHistory;
//  }

//  public BoardTreeNode createFromJson(String json) {
//    return getBoardTreeNodeFromJson(json);
//  }
}
