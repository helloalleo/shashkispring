//package com.workingbit.board.service;
//
//import com.workingbit.board.dao.BoardHistoryDao;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///**
// * Created by Aleksey Popryaduhin on 14:54 14/08/2017.
// */
//@Service
//public class BoardHistoryService {
//
//  private final BoardHistoryDao boardHistoryDao;
//
//  @Autowired
//  public BoardHistoryService(BoardHistoryDao boardHistoryDao) {
//    this.boardHistoryDao = boardHistoryDao;
//  }
//
////  public void addBoardAndSave(Board newBoard) {
////    BoardBox currentBoard = newBoard.getCurrentBoard();
////
////    System.out.println(boardHistoryDao.findAll());
////    // find board history
////    Optional<BoardHistory> boardHistoryOptional = getHistory(newBoard.getId());
////    BoardHistoryManager boardHistoryManager = new BoardHistoryManager(newBoard.getId());
////    if (boardHistoryOptional.isPresent()) {
////      boardHistoryManager = new BoardHistoryManager(boardHistoryOptional.get());
////    }
////    boardHistoryManager.addBoard(currentBoard);
////    boardHistoryDao.save(boardHistoryManager.getBoardHistory());
////  }
///*
//  public Map<String, Object> undo(String boardId) throws BoardServiceException {
//    // find history for given board
//    Optional<BoardHistory> boardHistoryOptional = boardHistoryDao.findByBoardId(boardId);
//    if (boardHistoryOptional.isPresent()) {
//      // initialize history manager with found history
//      BoardHistoryManager boardHistoryManager = new BoardHistoryManager(boardHistoryOptional.get());
//      // undo history
//      Optional<BoardBox> undo = boardHistoryManager.undo();
//      if (undo.isPresent()) {
//        // do move
//        BoardBox oldBoardContainer = boardHistoryOptional.get().getCurrent().getData();
//        BoardBox newBoardContainer = undo.get();
//
//        Map<String, Object> moveTo = new HashMap<String, Object>() {{
//          put(EnumBaseKeys.boardId.name(), boardId);
////          put(selectedSquare.name(), oldBoardContainer.getSelectedSquare());
////          put(targetSquare.name(), newBoardContainer.getSelectedSquare());
//          put(EnumBaseKeys.undoMove.name(), true);
////          put(allowed.name(), allowedMoves);
////          put(beaten.name(), beatenMoves);
//        }};
//
//        return moveTo;
//      }
//    }
//    return Collections.emptyMap();
//  }
//*/
////  public Optional<BoardHistory> getHistory(String boardId) {
////    return boardHistoryDao.findByBoardId(boardId);
////  }
//}
