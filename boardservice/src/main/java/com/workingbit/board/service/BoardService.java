package com.workingbit.board.service;

import com.workingbit.board.dao.BoardBoxDao;
import com.workingbit.board.dao.BoardDao;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.common.Log;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.BoardBox;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;
import com.workingbit.share.model.CreateBoardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.workingbit.board.service.BoardUtils.*;
import static com.workingbit.share.common.Utils.getRandomUUID;

/**
 * Created by Aleksey Popryaduhin on 13:45 09/08/2017.
 */
@Service
public class BoardService {

  private final BoardDao boardDao;
  private final BoardBoxDao boardBoxDao;

  @Autowired
  public BoardService(BoardDao boardDao,
                      BoardBoxDao boardBoxDao) {
    this.boardDao = boardDao;
    this.boardBoxDao = boardBoxDao;
  }

  public Board createBoard(CreateBoardRequest newBoardRequest) {
    Board board = initBoard(newBoardRequest.getFillBoard(), newBoardRequest.getBlack(),
        newBoardRequest.getRules());
    board.setId(getRandomUUID());
    save(board);
    return board;
  }

  public List<BoardBox> findAll(Integer limit) {
    return boardBoxDao.findAll(limit);
  }

  public Optional<Board> findById(String boardId) {
    Optional<Board> boardOptional = boardDao.findById(boardId);
    return boardOptional.map(BoardUtils::updateBoard);
  }

  public void delete(String boardId) {
    boardDao.delete(boardId);
  }

  /**
   * @return map of {allowed, beaten}
   * @throws BoardServiceException
   */
  public Board highlight(Board boardHighlight) throws BoardServiceException {
    Square selectedSquare = boardHighlight.getSelectedSquare();
    if (isValidHighlight(boardHighlight, selectedSquare)) {
      throw new BoardServiceException("Invalid highlight square");
    }
    return getHighlightedBoard(boardHighlight, selectedSquare);
  }

  private boolean isValidHighlight(Board boardHighlight, Square selectedSquare) {
    return selectedSquare == null
        || !selectedSquare.isOccupied()
        || selectedSquare.getDraught().isBlack() != boardHighlight.isBlack();
  }

  /**
   * @param previous map of {boardId: String, selectedSquare: Square, targetSquare: Square, allowed: List<Square>, beaten: List<Square>}
   * @param selectedSquare
   * @param nextSquare
   * @return Move info:
   * {v, h, targetSquare, queen} v - distance for moving vertical (minus up),
   * h - distance for move horizontal (minus left), targetSquare is a new square with
   * moved draught, queen is a draught has become the queen
   * @throws BoardServiceException
   */
  public Board move(Board previous, Square selectedSquare, Square nextSquare) throws BoardServiceException {
      Board moved = BoardUtils.moveDraught(selectedSquare, nextSquare, previous);
      if (moved == null) {
        throw new BoardServiceException("Unable to move draught");
      }
      moved.getPrevious().put(selectedSquare.getNotation(), previous.getId());
      previous.getNext().put(nextSquare.getNotation(), moved.getId());
      boardDao.save(previous);
      boardDao.save(moved);
      return moved;
  }

  public void save(Board board) {
    boardDao.save(board);
  }

  public Board addDraught(Board currentBoard, String notation, Draught draught) throws BoardServiceException {
    try {
      BoardUtils.addDraught(currentBoard, notation, draught);
      boardDao.save(currentBoard);
      return currentBoard;
    } catch (BoardServiceException e) {
      Log.error("Unable to add draught", e);
    }
    return null;
  }

  public Optional<Board> undo(Board board) {
    return boardDao.findById(board.getId())
        .map(currentBoard -> {
          Square previousSquare = currentBoard.getPreviousSquare();
          if (previousSquare != null) {
            String previousId = currentBoard.getPreviousBoard(previousSquare.getNotation());
            return boardDao.findById(previousId)
                .map(undoBoard -> {
                  undoBoard.setCursor(true);
                  currentBoard.setCursor(false);
                  undoBoard.setPreviousBoard(currentBoard.getSelectedSquare().getNotation(), currentBoard.getId());
                  return undoBoard;
                })
                .orElseGet(null);
          }
          return currentBoard;
        });
  }
}
