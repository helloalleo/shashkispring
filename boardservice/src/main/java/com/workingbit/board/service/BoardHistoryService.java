package com.workingbit.board.service;

import com.workingbit.board.dao.BoardDao;
import com.workingbit.board.dao.BoardHistoryDao;
import com.workingbit.history.domain.IBoardTree;
import com.workingbit.history.domain.impl.BoardHistory;
import com.workingbit.history.service.BoardHistoryManager;
import com.workingbit.share.domain.IBoard;
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
  private final BoardHistoryManager boardHistoryManager;

  @Autowired
  public BoardHistoryService(BoardHistoryDao boardHistoryDao,
                             BoardDao boardDao) {
    this.boardHistoryDao = boardHistoryDao;
    this.boardDao = boardDao;
    this.boardHistoryManager = BoardHistoryManager.getInstance();
  }

  public void addBoardAndSave(IBoard newBoard) {
    BoardContainer currentBoard = newBoard.getCurrentBoard();
    boardHistoryManager.addBoard(currentBoard);
    BoardHistory history = boardHistoryManager.getHistory(newBoard.getId());
    boardHistoryDao.save(history);
  }

  public Optional<Board> undo(Board board) {
    Optional<IBoardTree> boardTree = boardHistoryDao.findById(board.getId());
    Optional<BoardContainer> undo = boardHistoryManager.undo();
    if (undo.isPresent()) {
      board.setCurrentBoard(undo.get());
      boardDao.save(board);
      return Optional.of(board);
    }
    return Optional.empty();
  }

  public Optional<Board> redo(Board board) {
    Optional<BoardContainer> redo = boardHistoryManager.redo();
    if (redo.isPresent()) {
      board.setCurrentBoard(redo.get());
      boardDao.save(board);
      return Optional.of(board);
    }
    return Optional.empty();
  }
}
