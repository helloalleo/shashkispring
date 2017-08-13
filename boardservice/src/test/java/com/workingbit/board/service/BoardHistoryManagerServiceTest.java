package com.workingbit.board.service;

import com.workingbit.share.domain.IBoardContainer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

/**
 * Created by Aleksey Popryaduhin on 12:45 13/08/2017.
 */
public class BoardHistoryManagerServiceTest extends BaseServiceTest {

  @Autowired
  private BoardHistoryManagerService historyManagerService;

  @Test
  public void clear() throws Exception {
  }

  @Test
  public void addChangeable() throws Exception {
    IBoardContainer board = getBoard();
    historyManagerService.addChangeable(board);
    boolean canUndo = historyManagerService.canUndo();
    assertTrue(canUndo);
  }

  @Test
  public void canUndo() throws Exception {
  }

  @Test
  public void canRedo() throws Exception {
  }

  @Test
  public void undo() throws Exception {
  }

  @Test
  public void redo() throws Exception {
  }

  @Test
  public void getBoardHistory() throws Exception {
  }

}