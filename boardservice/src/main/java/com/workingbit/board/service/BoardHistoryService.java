package com.workingbit.board.service;

import com.workingbit.board.dao.BoardDao;
import com.workingbit.board.dao.BoardHistoryDao;
import com.workingbit.history.domain.impl.BoardHistory;
import com.workingbit.history.service.BoardHistoryManager;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.BoardContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Aleksey Popryaduhin on 14:54 14/08/2017.
 */
@Service
public class BoardHistoryService {

  private final BoardHistoryDao boardHistoryDao;
  private final BoardDao boardDao;

  @Autowired
  public BoardHistoryService(BoardHistoryDao boardHistoryDao,
                             BoardDao boardDao) {
    this.boardHistoryDao = boardHistoryDao;
    this.boardDao = boardDao;
  }

  public void addBoardAndSave(Board newBoard) {
    BoardContainer currentBoard = newBoard.getCurrentBoard();

    System.out.println(boardHistoryDao.findAll());
    // find board history
    Optional<BoardHistory> boardHistoryOptional = getHistory(newBoard.getId());
    BoardHistoryManager boardHistoryManager = new BoardHistoryManager(newBoard.getId());
    if (boardHistoryOptional.isPresent()) {
      boardHistoryManager = new BoardHistoryManager(boardHistoryOptional.get());
    }
    boardHistoryManager.addBoard(currentBoard);
    boardHistoryDao.save(boardHistoryManager.getBoardHistory());
  }

  public Optional<Board> undo(String boardId) {
    Optional<BoardHistory> boardHistoryOptional = boardHistoryDao.findByBoardId(boardId);
    if (boardHistoryOptional.isPresent()) {
      BoardHistoryManager boardHistoryManager = new BoardHistoryManager(boardHistoryOptional.get());
      Optional<BoardContainer> undo = boardHistoryManager.undo();
      if (undo.isPresent()) {
        boardHistoryDao.save(boardHistoryManager.getBoardHistory());
        Optional<Board> boardOptional = boardDao.findById(boardId);
        if (boardOptional.isPresent()) {
          Board board = boardOptional.get();
          board.setCurrentBoard(undo.get());
          boardDao.save(board);
          return Optional.of(board);
        }
      }
    }
    return Optional.empty();
  }

  public Optional<BoardHistory> getHistory(String boardId) {
    return boardHistoryDao.findByBoardId(boardId);
  }
}
