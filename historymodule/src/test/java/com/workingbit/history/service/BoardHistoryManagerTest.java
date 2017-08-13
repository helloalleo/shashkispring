package com.workingbit.history.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.share.domain.IBoard;
import com.workingbit.share.domain.impl.Board;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
    historyManagerService.addChangeable(board.getCurrentBoard());
    boolean canUndo = historyManagerService.canUndo();
    assertTrue(canUndo);
  }

  @Test
  public void undo() throws Exception {
  }

  @Test
  public void redo() throws Exception {
  }

  IBoard getBoard() {
    return new Board();
  }
}