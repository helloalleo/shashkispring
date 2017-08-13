package com.workingbit.history.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rutledgepaulv.prune.Tree;
import com.workingbit.share.domain.IBoard;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.BoardContainer;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by Aleksey Popryaduhin on 16:09 13/08/2017.
 */
public class BoardHistoryManagerTest {

  ObjectMapper mapper = new ObjectMapper();

  private BoardHistoryManager historyManagerService;

  @Before
  public void setUp() {
    this.historyManagerService = BoardHistoryManager.getInstance();
  }

  @Test
  public void addChangeable() throws Exception {
    IBoard board = getBoard();
    Tree.Node<Optional<BoardContainer>> node = historyManagerService.addBoard(board.getCurrentBoard());
    assertNotNull(node);
  }

  @Test
  public void undo() throws Exception {
    IBoard board = getBoard();
    historyManagerService.addBoard(board.getCurrentBoard());
    assertNotNull(board);
    boolean canUndo = historyManagerService.canUndo();
    assertTrue(canUndo);
  }

  @Test
  public void redo() throws Exception {
    IBoard board = getBoard();
    historyManagerService.addBoard(board.getCurrentBoard());
    historyManagerService.addBoard(board.getCurrentBoard());
    assertNotNull(board);
    boolean canUndo = historyManagerService.canUndo();
    assertTrue(canUndo);
    Optional<BoardContainer> undo = historyManagerService.undo();
    assertTrue(undo.isPresent());
    assertTrue(historyManagerService.canRedo());
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
    boolean canUndo = historyManagerService.canUndo();
    assertTrue(canUndo);
    Optional<BoardContainer> undo = historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");
    assertTrue(historyManagerService.canRedo());
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
    try {
      historyManagerService.undo();
    } catch (IllegalStateException ignore) {
      assertTrue(true);
    }
  }

  @Test
  public void redo_branch_custom() throws Exception {
    historyManagerService.addBoard(getBoard("1").getCurrentBoard());
    historyManagerService.addBoard(getBoard("2").getCurrentBoard());
    historyManagerService.addBoard(getBoard("3").getCurrentBoard());

    Optional<BoardContainer> undo = historyManagerService.undo();
    assertEquals(undo.get().getId(), "2");
    Tree.Node<Optional<BoardContainer>> branch4 = historyManagerService.addBoard(getBoard("4").getCurrentBoard());
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
    boolean canRedo = historyManagerService.canRedo();
    assertFalse(canRedo);
    try {
      historyManagerService.redo();
    } catch (IllegalStateException ignore) {
      assertTrue(true);
    }
  }

  IBoard getBoard(String id) {
    Board board = new Board();
    BoardContainer currentBoard = new BoardContainer();
    currentBoard.setId(id);
    board.setCurrentBoard(currentBoard);
    return board;
  }

  IBoard getBoard() {
    return new Board();
  }
}