//package com.workingbit.board.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.workingbit.share.common.Log;
//import com.workingbit.share.domain.impl.BoardBox;
//import com.workingbit.share.domain.impl.BoardHistory;
//import com.workingbit.share.domain.impl.BoardTreeNode;
//
//import javax.validation.constraints.NotNull;
//import java.util.Optional;
//
///**
// * Created by Aleksey Popryaduhin on 19:52 12/08/2017.
// */
//public class BoardHistoryManager {
//
//  private final BoardHistory boardHistory;
//    private ObjectMapper mapper = new ObjectMapper();
//  private BoardTreeNode current = new BoardTreeNode(null);
//
////  public void setBoardTree(BoardHistory boardHistory) {
////  }
//
//
//  public BoardHistoryManager(String boardId) {
//    boardHistory = new BoardHistory(boardId);
//  }
//
//  public BoardHistoryManager(BoardHistory boardHistory) {
//    this.boardHistory = boardHistory;
//    current = boardHistory.getCurrent();
//  }
//
//  public BoardHistory getBoardHistory() {
//    boardHistory.setRoot(getCurrent().getRootOfTree());
//    boardHistory.setCurrent(getCurrent());
//    try {
//      Log.debug("Board History " + mapper.writeValueAsString(boardHistory));
//    } catch (JsonProcessingException e) {
//      Log.error(e.getMessage());
//    }
//    return boardHistory;
//  }
//
//  /**
//   * Adds a Changeable to manage.
//   *
//   * @param boardBox
//   */
//  public BoardTreeNode addBoard(@NotNull BoardBox boardBox) {
//    BoardTreeNode child = new BoardTreeNode(boardBox);
//    current.addChild(child);
//    current = child;
//    return current;
//  }
//
//  private void moveUp() {
//    current = current.getParent();
//  }
//
//  private void moveDown(BoardTreeNode branch) {
//    current = branch;
//  }
//
//  private void moveDown() {
//    current = current.getChildren().get(0);
//  }
//
//  private boolean canUndo() {
//    return current.getParent()
//        .getData() != null;
//  }
//
//  private boolean canRedo() {
//    return !current.getChildren().isEmpty();
//  }
//
//  private boolean canRedo(BoardTreeNode branch) {
//    return current.getChildren().contains(branch);
//  }
//
//  private BoardTreeNode getCurrent() {
//    return current;
//  }
//
////  private BoardTreeNode getBoardTreeNodeFromJson(String json) {
////    Log.debug("Read object from json " + json);
////    try {
////      return mapper.readValue(json, BoardTreeNode.class);
////    } catch (IOException e) {
////      return null;
////    }
////  }
//
////  BoardTreeNode getBoardTreeNode() {
////    return current.getRootOfTree();
////  }
//
////  private Tree<Tree.Node<BoardBox>> getTree(Tree.Node<Optional<BoardBox>> node) {
////    return node.asTree()
////        .mapAsNodes(optionalNode -> Tree.node(optionalNode.getData().orElse(null)))
////        .deepClone(boardBoxNode -> boardBoxNode);
////  }
//
//  public String serializeToJsonBoardTreeNode() {
//    try {
//      BoardTreeNode boardTree = getCurrent();
//      return mapper.writeValueAsString(boardTree);
//    } catch (JsonProcessingException e) {
//      return "";
//    }
//  }
//
//  /**
//   * Undoes the Changeable at the current index.
//   *
//   * @throws IllegalStateException if canUndo returns false.
//   */
//  public Optional<BoardBox> undo() {
//    //validate
//    if (!canUndo()) {
//      return Optional.empty();
//    }
//    //set index
//    moveUp();
//    //undo
//    BoardBox boardBoxOptional = getCurrent().getData();
//    boardBoxOptional.undo();
//    return Optional.of(boardBoxOptional);
//  }
//
//  /**
//   * Redoes the Changable at the current index.
//   *
//   * @throws IllegalStateException if canRedo returns false.
//   */
//  public Optional<BoardBox> redo(BoardTreeNode branch) {
//    //validate
//    if (!canRedo(branch)) {
//      return Optional.empty();
//    }
//    //reset index
//    moveDown(branch);
//    //redo
//    BoardBox boardBox = getCurrent().getData();
//    boardBox.redo();
//    return Optional.of(boardBox);
//  }
//
//  public Optional<BoardBox> redo() {
//    if (!canRedo()) {
//      return Optional.empty();
//    }
//    moveDown();
//    BoardBox boardBox = getCurrent().getData();
//    boardBox.redo();
//    return Optional.of(boardBox);
//  }
//
////  public BoardHistory getHistoryByBoardId(String id) {
////    BoardHistory boardHistory = new BoardHistory();
////    boardHistory.setHistory(history);
////    boardHistory.setBoardId(id);
////    return boardHistory;
////  }
//
////  public BoardTreeNode createFromJson(String json) {
////    return getBoardTreeNodeFromJson(json);
////  }
//}
