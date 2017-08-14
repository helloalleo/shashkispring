package com.workingbit.history.domain.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rutledgepaulv.prune.Tree;
import com.workingbit.history.converter.HistoryModule;
import com.workingbit.history.domain.IBoardHistory;
import com.workingbit.share.common.Log;
import com.workingbit.share.domain.impl.BoardContainer;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by Aleksey Popryaduhin on 10:02 13/08/2017.
 */
@Data
public class BoardHistory implements IBoardHistory {

  private ObjectMapper mapper = new ObjectMapper();
  private Tree.Node<Optional<BoardContainer>> current = Tree.node(Optional.empty());

  public BoardHistory() {
    mapper.registerModule(new HistoryModule());
  }

  @Override
  public Tree.Node<Optional<BoardContainer>> addBoard(@NotNull BoardContainer boardContainer) {
    Tree.Node<Optional<BoardContainer>> child = Tree.node(Optional.of(boardContainer));
    current.addChildNode(child);
    current = child;
    return current;
  }

  @Override
  public void moveUp() {
    current = current.getParent().orElseGet(null);
  }

  @Override
  public void moveDown(Tree.Node<Optional<BoardContainer>> branch) {
    current = branch;
  }

  @Override
  public void moveDown() {
    current = current.getChildren().get(0);
  }

  @Override
  public boolean canUndo() {
    return current.getParent()
        .orElseThrow(() -> new IllegalStateException("Can't undo"))
        .getData() != null;
  }

  @Override
  public boolean canRedo() {
    return !current.getChildren().isEmpty();
  }

  @Override
  public boolean canRedo(Tree.Node<Optional<BoardContainer>> branch) {
    return current.getChildren().contains(branch);
  }

  @Override
  public Tree.Node<Optional<BoardContainer>> getLast() {
    return current;
  }

  @Override
  public Tree<Tree.Node<BoardContainer>> getTree() {
    return getTree(current);
  }

  @Override
  public String getJson() {
    return createBoardTreeNode();
  }

  @Override
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
}
