package com.workingbit.history.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rutledgepaulv.prune.Tree;
import com.workingbit.history.domain.impl.BoardHistory;
import com.workingbit.history.domain.impl.BoardTreeNode;
import com.workingbit.share.common.Log;
import com.workingbit.share.domain.impl.BoardContainer;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

/**
 * Created by Aleksey Popryaduhin on 19:52 12/08/2017.
 */
public class BoardHistoryManager {

  private ObjectMapper mapper = new ObjectMapper();
  private BoardTreeNode current = new BoardTreeNode();

  public void setBoardTree(BoardHistory boardHistory) {

    Tree<BoardContainer> newTree = Tree.empty();
    BoardTreeNode boardTreeNode = getBoardTreeNodeFromJson(boardHistory.getHistory());
    Iterator<BoardTreeNode> boardTreeNodeIterator = boardTreeNode.breadthFirstIter();
    while (boardTreeNodeIterator.hasNext()) {
    }
  }

  /**
   * Adds a Changeable to manage.
   *
   * @param boardContainer
   */
  public Tree.Node<Optional<BoardContainer>> addBoard(@NotNull BoardContainer boardContainer) {
    Tree.Node<Optional<BoardContainer>> child = Tree.node(Optional.of(boardContainer));
    current.addChildNode(child);
    current = child;
    return current;
  }

  private void moveUp() {
    current = current.getParent().orElseGet(null);
  }

  private void moveDown(Tree.Node<Optional<BoardContainer>> branch) {
    current = branch;
  }

  private void moveDown() {
    current = current.getChildren().get(0);
  }

  private boolean canUndo() {
    return current.getParent()
        .orElseThrow(() -> new IllegalStateException("Can't undo"))
        .getData() != null;
  }

  private boolean canRedo() {
    return !current.getChildren().isEmpty();
  }

  private boolean canRedo(Tree.Node<Optional<BoardContainer>> branch) {
    return current.getChildren().contains(branch);
  }

  private Tree.Node<Optional<BoardContainer>> getLast() {
    return current;
  }

  private BoardTreeNode getBoardTreeNodeFromJson(String json) {
    Log.debug("Read object from json " + json);
    try {
      return mapper.readValue(json, BoardTreeNode.class);
    } catch (IOException e) {
      return null;
    }
  }

  private BoardTreeNode getBoardTreeNode() {
    Tree.Node<Optional<BoardContainer>> rootOfTree = current.getRootOfTree();
    final BoardTreeNode[] indexNode = {new BoardTreeNode()};
    rootOfTree.asTree().breadthFirstVisit(optionalNode -> {
      BoardTreeNode current = new BoardTreeNode();
      BoardContainer data = optionalNode.orElse(null);
      current.setData(data);
      current.setParent(indexNode[0]);
      indexNode[0].getChildren().add(current);
      indexNode[0] = current;
      return true;
    });
    return indexNode[0];
  }

  private Tree<Tree.Node<BoardContainer>> getTree(Tree.Node<Optional<BoardContainer>> node) {
    return node.asTree()
        .mapAsNodes(optionalNode -> Tree.node(optionalNode.getData().orElse(null)))
        .deepClone(boardContainerNode -> boardContainerNode);
  }

  private String serializeToJsonBoardTreeNode() {
    try {
      BoardTreeNode boardTree = getBoardTreeNode();
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
    Optional<BoardContainer> boardContainerOptiona = getLast().getData();
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
    if (!canRedo(branch)) {
      return Optional.empty();
    }
    //reset index
    moveDown(branch);
    //redo
    Optional<BoardContainer> boardContainerOptional = getLast().getData();
    return boardContainerOptional
        .map(BoardContainer::redo);
  }

  public Optional<BoardContainer> redo() {
    if (!canRedo()) {
      return Optional.empty();
    }
    moveDown();
    Optional<BoardContainer> boardContainerOptional = getLast().getData();
    return boardContainerOptional
        .map(BoardContainer::redo);
  }

  public BoardHistory getHistory(String id) {
    BoardHistory boardHistory = new BoardHistory();
    String history = serializeToJsonBoardTreeNode();
    boardHistory.setHistory(history);
    boardHistory.setBoardId(id);
    return boardHistory;
  }

  private BoardTreeNode createFromJson(String json) {
    return getBoardTreeNodeFromJson(json);
  }
}
