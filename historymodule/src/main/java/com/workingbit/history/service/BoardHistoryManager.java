package com.workingbit.history.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rutledgepaulv.prune.Tree;
import com.workingbit.history.domain.impl.BoardTreeNode;
import com.workingbit.share.common.Log;
import com.workingbit.share.domain.impl.BoardContainer;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by Aleksey Popryaduhin on 19:52 12/08/2017.
 */
public class BoardHistoryManager {

  private static BoardHistoryManager INSTANCE = new BoardHistoryManager();

  /**
   * Creates a new ChangeManager object which is initially empty.
   */
  public BoardHistoryManager() {
  }

  public static BoardHistoryManager getInstance() {
    return INSTANCE;
  }

  private ObjectMapper mapper = new ObjectMapper();
  private Tree.Node<Optional<BoardContainer>> current = Tree.node(Optional.empty());

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

  public void moveUp() {
    current = current.getParent().orElseGet(null);
  }

  public void moveDown(Tree.Node<Optional<BoardContainer>> branch) {
    current = branch;
  }

  public void moveDown() {
    current = current.getChildren().get(0);
  }

  public boolean canUndo() {
    return current.getParent()
        .orElseThrow(() -> new IllegalStateException("Can't undo"))
        .getData() != null;
  }

  public boolean canRedo() {
    return !current.getChildren().isEmpty();
  }

  public boolean canRedo(Tree.Node<Optional<BoardContainer>> branch) {
    return current.getChildren().contains(branch);
  }

  public Tree.Node<Optional<BoardContainer>> getLast() {
    return current;
  }

  public Tree<Tree.Node<BoardContainer>> getTree() {
    return getTree(current);
  }

  public String getJson() {
    return createBoardTreeNode();
  }

  public BoardTreeNode fromJson(String json) {
    Log.debug("Read object from json " + json);
    try {
      return mapper.readValue(json, BoardTreeNode.class);
    } catch (IOException e) {
      return null;
    }
  }

  private Tree<Tree.Node<BoardContainer>> getTree(Tree.Node<Optional<BoardContainer>> node) {
    return node.asTree()
        .mapAsNodes(optionalNode -> Tree.node(optionalNode.getData().orElse(null)))
        .deepClone(boardContainerNode -> boardContainerNode);
  }

  private String createBoardTreeNode() {
    try {
      BoardTreeNode boardTree = getBoardTree();
      return mapper.writeValueAsString(boardTree);
    } catch (JsonProcessingException e) {
      return "";
    }
  }

  public BoardTreeNode getBoardTree() {
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

  /**
   * Undoes the Changeable at the current index.
   *
   * @throws IllegalStateException if canUndo returns false.
   */
  public Optional<BoardContainer> undo() {
    //validate
    if (!canUndo()) {
      throw new IllegalStateException("Cannot undo. Index is out of range.");
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
      throw new IllegalStateException("Cannot redo. Index is out of range.");
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
      throw new IllegalStateException("Cannot redo. Index is out of range.");
    }
    moveDown();
    Optional<BoardContainer> boardContainerOptional = getLast().getData();
    return boardContainerOptional
        .map(BoardContainer::redo);
  }

  public String getHistory() {
    return getJson();
  }

  public BoardTreeNode createFromJson(String json) {
    return fromJson(json);
  }
}
