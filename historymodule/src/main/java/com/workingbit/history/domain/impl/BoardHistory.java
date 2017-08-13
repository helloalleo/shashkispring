package com.workingbit.history.domain.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.github.rutledgepaulv.prune.Tree;
import com.workingbit.board.common.DBConstants;
import com.workingbit.history.domain.IBoardHistory;
import com.workingbit.share.domain.impl.BoardContainer;
import lombok.Data;

import java.util.Optional;

/**
 * Created by Aleksey Popryaduhin on 10:02 13/08/2017.
 */
@Data
@DynamoDBTable(tableName = DBConstants.BOARD_HISTORY_TABLE)
public class BoardHistory implements IBoardHistory{

  private Tree.Node<Optional<BoardContainer>> current = Tree.node(Optional.empty());

  @Override
  public void addBoard(Optional<BoardContainer> boardContainer) {
    Tree.Node<Optional<BoardContainer>> child = Tree.node(boardContainer);
    current.addChildNode(child);
    current = child;
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
}
