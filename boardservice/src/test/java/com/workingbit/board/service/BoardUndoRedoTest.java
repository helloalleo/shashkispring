package com.workingbit.board.service;

import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.BoardBox;
import com.workingbit.share.domain.impl.Square;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Aleksey Popryaduhin on 16:59 15/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BoardUndoRedoTest extends BaseServiceTest {

  @Autowired
  private BoardService boardService;

  @Test
  public void should_undo() {
    BoardBox boardBox = getBoard();
    Board board = boardBox.getBoard();
    String c3 = "c3";
    BoardUtils.addDraught(board, c3, false);
    Square squareC3 = BoardUtils.findSquareByNotation(board, c3).get();
    String d4 = "d4";
    Square squareD4 = BoardUtils.findSquareByNotation(board, d4).get();
    squareD4.setHighlighted(true);
    board.setSelectedSquare(squareC3);
    board.setNextSquare(squareD4);
    boardService.move(squareC3, squareD4, board);
    assertFalse(squareC3.isOccupied());
    assertTrue(squareD4.isOccupied());
    board = boardService.undo(board).get();
    squareC3 = BoardUtils.findSquareLink(board, squareC3).get();
    assertTrue(squareC3.isOccupied());
    squareD4 = BoardUtils.findSquareLink(board, squareD4).get();
    assertFalse(squareD4.isOccupied());
  }

  @Test
  public void should_redo() {
    BoardBox boardBox = getBoard();
    Board board = boardBox.getBoard();
    String c3 = "c3";
    BoardUtils.addDraught(board, c3, false);
    Square squareC3 = BoardUtils.findSquareByNotation(board, c3).get();
    String d4 = "d4";
    Square squareD4 = BoardUtils.findSquareByNotation(board, d4).get();
    squareD4.setHighlighted(true);
    board.setSelectedSquare(squareC3);
    board.setNextSquare(squareD4);
    boardService.move(squareC3, squareD4, board);
    assertFalse(squareC3.isOccupied());
    assertTrue(squareD4.isOccupied());
    board = boardService.undo(board).get();
    squareC3 = BoardUtils.findSquareLink(board, squareC3).get();
    assertTrue(squareC3.isOccupied());
    squareD4 = BoardUtils.findSquareLink(board, squareD4).get();
    assertFalse(squareD4.isOccupied());

    board = boardService.redo(board).get();
    squareC3 = BoardUtils.findSquareLink(board, squareC3).get();
    assertFalse(squareC3.isOccupied());
    squareD4 = BoardUtils.findSquareLink(board, squareD4).get();
    assertTrue(squareD4.isOccupied());
  }

//  @Test
//  public void should_save_history() throws Exception, BoardServiceError {
//    BoardBox board = getBoard();
////    boardHistoryService.addBoardAndSave(board);
//    Optional<BoardHistory> boardHistory = boardHistoryService.getHistory(board.getId());
//    assertTrue(boardHistory.isPresent());
//    assertEquals(boardHistory.get().getCurrent().getData(), board);
//  }
//
//  @Test
//  public void should_save_two_history() throws Exception, BoardServiceError {
//    BoardBox board = getBoard();
////    boardHistoryService.addBoardAndSave(board);
//    Optional<BoardHistory> boardHistory = boardHistoryService.getHistory(board.getId());
//    assertTrue(boardHistory.isPresent());
//    assertEquals(boardHistory.get().getCurrent().getData(), board);
//
////    boardHistoryService.addBoardAndSave(board);
//    boardHistory = boardHistoryService.getHistory(board.getId());
//    assertTrue(boardHistory.isPresent());
//    assertEquals(boardHistory.get().getCurrent().getData(), board);
//  }

//  @Test
//  public void undo() throws Exception {
//  }

}