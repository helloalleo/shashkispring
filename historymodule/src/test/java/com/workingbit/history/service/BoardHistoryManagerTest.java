package com.workingbit.history.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.history.domain.impl.BoardHistory;
import com.workingbit.history.domain.impl.BoardTreeNode;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.BoardContainer;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by Aleksey Popryaduhin on 16:09 13/08/2017.
 */
public class BoardHistoryManagerTest {

  ObjectMapper mapper = new ObjectMapper();

  private BoardHistoryManager historyManagerService;

  @Before
  public void setUp() {
    this.historyManagerService = new BoardHistoryManager(new BoardHistory(UUID.randomUUID().toString(), "", new BoardTreeNode(null), new BoardTreeNode(null)));
  }

  @Test
  public void addChangeable() throws Exception {
    Board board = getBoard();
    BoardTreeNode node = historyManagerService.addBoard(board.getCurrentBoard());
    assertNotNull(node);
  }

  @Test
  public void undo() throws Exception {
    Board board = getBoard();
    historyManagerService.addBoard(board.getCurrentBoard());
    assertNotNull(board);
  }

  @Test
  public void redo() throws Exception {
    Board board = getBoard();
    historyManagerService.addBoard(board.getCurrentBoard());
    historyManagerService.addBoard(board.getCurrentBoard());
    assertNotNull(board);
    Optional<BoardContainer> undo = historyManagerService.undo();
    assertTrue(undo.isPresent());
    Optional<BoardContainer> redo = historyManagerService.redo();
    assertTrue(redo.isPresent());
  }

  /**
   * 1
   * 2--
   * 4-
   * 5
   * 3-
   *
   * @throws Exception
   */
  @Test
  public void redo_branch_first() throws Exception {
    historyManagerService.addBoard(getBoard("1").getCurrentBoard());
    historyManagerService.addBoard(getBoard("2").getCurrentBoard());
    historyManagerService.addBoard(getBoard("3").getCurrentBoard());
    Optional<BoardContainer> undo = historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");
    Optional<BoardContainer> redo = historyManagerService.redo();
    assertEquals(redo.get().getId(), "3");
    undo = historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");

    historyManagerService.addBoard(getBoard("4").getCurrentBoard());
    historyManagerService.addBoard(getBoard("5").getCurrentBoard());
    undo = historyManagerService.undo();
    assertEquals(undo.get().getId(), "4");
    undo = historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");
    undo = historyManagerService.undo();
    assertEquals(undo.get().getId(), "1");
    historyManagerService.undo();
      assertFalse(historyManagerService.undo().isPresent());
  }

  @Test
  public void redo_branch_custom() throws Exception {
    historyManagerService.addBoard(getBoard("1").getCurrentBoard());
    historyManagerService.addBoard(getBoard("2").getCurrentBoard());
    historyManagerService.addBoard(getBoard("3").getCurrentBoard());

    Optional<BoardContainer> undo = historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");
    BoardTreeNode branch4 = historyManagerService.addBoard(getBoard("4").getCurrentBoard());
    historyManagerService.addBoard(getBoard("44").getCurrentBoard());
    historyManagerService.undo();
    historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");
    historyManagerService.addBoard(getBoard("5").getCurrentBoard());
    undo = historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");
    undo = historyManagerService.redo(branch4);
    assertEquals(undo.get().getId(), "4");
    undo = historyManagerService.redo();
    assertEquals(undo.get().getId(), "44");
    assertFalse(historyManagerService.redo().isPresent());
  }

  @Test
  public void undo_redo_with_recreated_history_manager() throws Exception {
    historyManagerService.addBoard(getBoard("1").getCurrentBoard());
    historyManagerService.addBoard(getBoard("2").getCurrentBoard());
    historyManagerService.addBoard(getBoard("3").getCurrentBoard());

    Optional<BoardContainer> undo = historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");
    historyManagerService.addBoard(getBoard("4").getCurrentBoard());
    historyManagerService.addBoard(getBoard("44").getCurrentBoard());
    BoardHistory boardHistory = historyManagerService.getBoardHistory();
    historyManagerService.undo();
    historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");
    historyManagerService.addBoard(getBoard("5").getCurrentBoard());
    undo = historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");

    // should start from 44
    BoardHistoryManager boardHistoryManager = new BoardHistoryManager(boardHistory);
    undo = boardHistoryManager.redo();
    assertFalse(undo.isPresent());
    undo = boardHistoryManager.undo();
    assertEquals(undo.get().getId(), "4");
    undo = boardHistoryManager.undo();
    assertEquals(undo.get().getId(), "2");
    undo = boardHistoryManager.undo();
    assertEquals(undo.get().getId(), "1");
  }

//  @Test
//  public void should_return_history_json() {
//    historyManagerService.addBoard(getBoard("1").getCurrentBoard());
//    historyManagerService.addBoard(getBoard("2").getCurrentBoard());
//    historyManagerService.addBoard(getBoard("3").getCurrentBoard());
//
//    BoardHistory history = historyManagerService.getHistoryByBoardId("");
//    assertTrue(history.getHistory().startsWith("["));
//    assertTrue(history.getHistory().length() > 10);
//  }

//  @Test
//  public void should_deserialize_from_json() {
//    historyManagerService.addBoard(getBoard("1").getCurrentBoard());
//    historyManagerService.addBoard(getBoard("2").getCurrentBoard());
//    historyManagerService.addBoard(getBoard("3").getCurrentBoard());
//
//    BoardHistory history = historyManagerService.getHistoryByBoardId("");
//    BoardTreeNode boardTreeNodeOrig = historyManagerService.getBoardTreeNode();
//    BoardTreeNode boardTreeNode = historyManagerService.createFromJson(history.getHistory());
//    assertNotNull(boardTreeNode);
//    assertEquals(boardTreeNodeOrig, boardTreeNode);
//  }

//  @Test
//  public void should_deserialize_from_json_tree() {
//    historyManagerService.addBoard(getBoard("1").getCurrentBoard());
//    historyManagerService.addBoard(getBoard("2").getCurrentBoard());
//    historyManagerService.addBoard(getBoard("3").getCurrentBoard());
//
//    historyManagerService.undo();
//    BoardTreeNode branch4 = historyManagerService.addBoard(getBoard("4").getCurrentBoard());
//    historyManagerService.addBoard(getBoard("44").getCurrentBoard());
//    historyManagerService.undo();
//    historyManagerService.undo();
//    historyManagerService.addBoard(getBoard("5").getCurrentBoard());
//    historyManagerService.undo();
//    historyManagerService.redo(branch4);
//    historyManagerService.redo();
//
//    BoardHistory history = historyManagerService.getHistoryByBoardId("");
//    BoardTreeNode boardTreeNodeOrig = historyManagerService.getBoardTreeNode();
//    BoardTreeNode boardTreeNode = historyManagerService.createFromJson(history.getHistory());
//    assertNotNull(boardTreeNode);
//    assertEquals(boardTreeNodeOrig, boardTreeNode);
//  }

  Board getBoard(String id) {
    Board board = new Board();
    BoardContainer currentBoard = new BoardContainer();
    currentBoard.setId(id);
    board.setCurrentBoard(currentBoard);
    return board;
  }

  Board getBoard() {
    return getBoard(UUID.randomUUID().toString());
  }
}