package com.workingbit.history.domain.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rutledgepaulv.prune.Tree;
import com.workingbit.history.converter.HistoryModule;
import com.workingbit.history.domain.IBoardHistory;
import com.workingbit.share.domain.impl.BoardContainer;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Iterator;
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
    boardContainer.setJson(toJson(child));
    current.addChildNode(child);
    current = child;
    return current;
  }

  private String toJson(Tree.Node<Optional<BoardContainer>> child) {
    Iterator<Tree.Node<Tree.Node<BoardContainer>>> nodeIterator = toTree(child).breadthFirstIter();
    StringBuilder stringBuilder = new StringBuilder();
    while (nodeIterator.hasNext()) {
      Tree.Node<Tree.Node<BoardContainer>> next = nodeIterator.next();
      try {
        String s = mapper.writeValueAsString(next);
        stringBuilder.append(s);
      } catch (JsonProcessingException ignore) {
        stringBuilder.append(child.toString());
      }
    }
    return stringBuilder.toString();
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

  public boolean canUndo() {
    return current.getParent()
        .orElseThrow(() -> new IllegalStateException("Can't undo"))
        .getData() != null;
  }

  public boolean canRedo() {
    return !current.getChildren().isEmpty();
  }

  @Override
  public boolean canRedo(Tree.Node<Optional<BoardContainer>> branch) {
    return current.getChildren().contains(branch);
  }

  public Tree.Node<Optional<BoardContainer>> getLast() {
    return current;
  }

  public Tree<Tree.Node<BoardContainer>> toTree(Tree.Node<Optional<BoardContainer>> node) {
    return node.asTree()
        .mapAsNodes(optionalNode -> Tree.node(optionalNode.getData().orElse(null)))
        .deepClone(boardContainerNode -> boardContainerNode);
  }

  public String toJson() {
    return "";
  }
}
