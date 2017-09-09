package com.workingbit.history.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.history.domain.impl.BoardHistory;
import com.workingbit.history.domain.impl.BoardTreeNode;
import com.workingbit.share.domain.impl.BoardContainer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by Aleksey Popryaduhin on 16:09 13/08/2017.
 */
public class BoardHistoryManagerTest {

  private BoardHistoryManager historyManagerService;
  private ObjectMapper mapper = new ObjectMapper();

  @Before
  public void setUp() {
    this.historyManagerService = new BoardHistoryManager(new BoardHistory(UUID.randomUUID().toString(), "", new BoardTreeNode(null), new BoardTreeNode(null)));
  }

  @Test
  public void addChangeable() throws Exception {
    BoardContainer board = getBoard();
    BoardTreeNode node = historyManagerService.addBoard(board);
    assertNotNull(node);
  }

  @Test
  public void undo() throws Exception {
    BoardContainer board = getBoard();
    historyManagerService.addBoard(board);
    assertNotNull(board);
  }

  @Test
  public void redo() throws Exception {
    BoardContainer board = getBoard();
    historyManagerService.addBoard(board);
    historyManagerService.addBoard(board);
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
    historyManagerService.addBoard(getBoard("1"));
    historyManagerService.addBoard(getBoard("2"));
    historyManagerService.addBoard(getBoard("3"));
    Optional<BoardContainer> undo = historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");
    Optional<BoardContainer> redo = historyManagerService.redo();
    assertEquals(redo.get().getId(), "3");
    undo = historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");

    historyManagerService.addBoard(getBoard("4"));
    historyManagerService.addBoard(getBoard("5"));
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
    historyManagerService.addBoard(getBoard("1"));
    historyManagerService.addBoard(getBoard("2"));
    historyManagerService.addBoard(getBoard("3"));

    Optional<BoardContainer> undo = historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");
    BoardTreeNode branch4 = historyManagerService.addBoard(getBoard("4"));
    historyManagerService.addBoard(getBoard("44"));
    historyManagerService.undo();
    historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");
    historyManagerService.addBoard(getBoard("5"));
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
    historyManagerService.addBoard(getBoard("1"));
    historyManagerService.addBoard(getBoard("2"));
    historyManagerService.addBoard(getBoard("3"));

    Optional<BoardContainer> undo = historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");
    historyManagerService.addBoard(getBoard("4"));
    historyManagerService.addBoard(getBoard("44"));
    BoardHistory boardHistory = historyManagerService.getBoardHistory();
    historyManagerService.undo();
    historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");
    historyManagerService.addBoard(getBoard("5"));
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

  @Test
  public void should_serialize_deserialize() throws IOException {
    historyManagerService.addBoard(getBoard("1"));
    BoardHistory boardHistory = historyManagerService.getBoardHistory();
    String s = mapper.writeValueAsString(boardHistory);
    assertFalse(s.isEmpty());
    BoardHistory boardHistory1 = mapper.readValue(s, BoardHistory.class);
    assertNotNull(boardHistory1);
  }
//  @Test
//  public void should_return_history_json() {
//    historyManagerService.addBoard(getBoard("1"));
//    historyManagerService.addBoard(getBoard("2"));
//    historyManagerService.addBoard(getBoard("3"));
//
//    BoardHistory history = historyManagerService.getHistoryByBoardId("");
//    assertTrue(history.getHistory().startsWith("["));
//    assertTrue(history.getHistory().length() > 10);
//  }

//  @Test
//  public void should_deserialize_from_json() {
//    historyManagerService.addBoard(getBoard("1"));
//    historyManagerService.addBoard(getBoard("2"));
//    historyManagerService.addBoard(getBoard("3"));
//
//    BoardHistory history = historyManagerService.getHistoryByBoardId("");
//    BoardTreeNode boardTreeNodeOrig = historyManagerService.getBoardTreeNode();
//    BoardTreeNode boardTreeNode = historyManagerService.createFromJson(history.getHistory());
//    assertNotNull(boardTreeNode);
//    assertEquals(boardTreeNodeOrig, boardTreeNode);
//  }

//  @Test
//  public void should_deserialize_from_json_tree() {
//    historyManagerService.addBoard(getBoard("1"));
//    historyManagerService.addBoard(getBoard("2"));
//    historyManagerService.addBoard(getBoard("3"));
//
//    historyManagerService.undo();
//    BoardTreeNode branch4 = historyManagerService.addBoard(getBoard("4"));
//    historyManagerService.addBoard(getBoard("44"));
//    historyManagerService.undo();
//    historyManagerService.undo();
//    historyManagerService.addBoard(getBoard("5"));
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

  BoardContainer getBoard(String id) {
    BoardContainer board = new BoardContainer();
    BoardContainer currentBoard = new BoardContainer();
    currentBoard.setId(id);
    return board;
  }

  BoardContainer getBoard() {
    return getBoard(UUID.randomUUID().toString());
  }
}