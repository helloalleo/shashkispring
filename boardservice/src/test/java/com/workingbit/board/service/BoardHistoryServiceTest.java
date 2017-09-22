//package com.workingbit.board.service;
//
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * Created by Aleksey Popryaduhin on 16:59 15/08/2017.
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class BoardHistoryServiceTest extends BaseServiceTest {
//
//  @Autowired
//  private BoardHistoryService boardHistoryService;
//
////  @Test
////  public void should_save_history() throws Exception, BoardServiceException {
////    BoardBox board = getBoard();
//////    boardHistoryService.addBoardAndSave(board);
////    Optional<BoardHistory> boardHistory = boardHistoryService.getHistory(board.getId());
////    assertTrue(boardHistory.isPresent());
////    assertEquals(boardHistory.get().getCurrent().getData(), board);
////  }
////
////  @Test
////  public void should_save_two_history() throws Exception, BoardServiceException {
////    BoardBox board = getBoard();
//////    boardHistoryService.addBoardAndSave(board);
////    Optional<BoardHistory> boardHistory = boardHistoryService.getHistory(board.getId());
////    assertTrue(boardHistory.isPresent());
////    assertEquals(boardHistory.get().getCurrent().getData(), board);
////
//////    boardHistoryService.addBoardAndSave(board);
////    boardHistory = boardHistoryService.getHistory(board.getId());
////    assertTrue(boardHistory.isPresent());
////    assertEquals(boardHistory.get().getCurrent().getData(), board);
////  }
//
////  @Test
////  public void undo() throws Exception {
////  }
//
//}