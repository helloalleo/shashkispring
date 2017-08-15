package com.workingbit.board.service;

import com.workingbit.history.domain.impl.BoardHistory;
import com.workingbit.share.domain.impl.Board;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by Aleksey Popryaduhin on 16:59 15/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BoardHistoryServiceTest extends BaseServiceTest {

  @Autowired
  private BoardHistoryService boardHistoryService;

  @Test
  public void addBoardAndSave() throws Exception {
    Board board = getBoard();
    assertNotNull(board.getId());
    boardHistoryService.addBoardAndSave(board);
    Optional<BoardHistory> boardHistory = boardHistoryService.getHistory(board.getId());
    assertTrue(boardHistory.isPresent());
    assertEquals(boardHistory.get().getCurrent().getData(), board.getCurrentBoard());

    // change board
    Board board1 = ObjectUtils.clone(board);
    board1.getCurrentBoard().getSquares().get(0).setHighlighted(true);

    boardHistoryService.addBoardAndSave(board1);
    boardHistory = boardHistoryService.getHistory(board.getId());
    assertTrue(boardHistory.isPresent());
    assertEquals(boardHistory.get().getCurrent().getData(), board1.getCurrentBoard());

    boardHistoryService.addBoardAndSave(board1);
    Optional<Board> undo = boardHistoryService.undo(board1.getId());
    assertTrue(undo.isPresent());
    assertEquals(undo.get(), board);
  }

  @Test
  public void undo() throws Exception {
  }

}