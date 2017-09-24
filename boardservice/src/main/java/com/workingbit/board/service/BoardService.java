package com.workingbit.board.service;

import com.workingbit.board.dao.BoardBoxDao;
import com.workingbit.board.dao.BoardDao;
import com.workingbit.board.exception.BoardServiceError;
import com.workingbit.share.common.Utils;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.BoardBox;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;
import com.workingbit.share.model.CreateBoardRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.workingbit.board.service.BoardUtils.getHighlightedBoard;
import static com.workingbit.board.service.BoardUtils.initBoard;

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
    Utils.setRandomIdAndCreatedAt(board);
    board.setCursor(true);
    save(board);
    return board;
  }

  public List<BoardBox> findAll(Integer limit) {
    return boardBoxDao.findAll(limit);
  }

  public Optional<Board> findById(String boardId) {
    Optional<Board> boardOptional = boardDao.findByKey(boardId);
    return boardOptional.map(BoardUtils::updateBoard);
  }

  public void delete(String boardId) {
    boardDao.delete(boardId);
  }

  /**
   * @return map of {allowed, beaten}
   * @throws BoardServiceError
   */
  public Board highlight(Board boardHighlight) {
    Square selectedSquare = boardHighlight.getSelectedSquare();
    if (isValidHighlight(boardHighlight, selectedSquare)) {
      throw new BoardServiceError("Invalid highlight square");
    }
    getHighlightedBoard(boardHighlight, selectedSquare);
    return boardHighlight;
  }

  private boolean isValidHighlight(Board boardHighlight, Square selectedSquare) {
    return selectedSquare == null
        || !selectedSquare.isOccupied()
        || selectedSquare.getDraught().isBlack() != boardHighlight.isBlack();
  }

  /**
   * @param selectedSquare
   * @param nextSquare
   * @param checkAllowed
   * @param toMove         map of {boardId: String, selectedSquare: Square, targetSquare: Square, allowed: List<Square>, beaten: List<Square>}  @return Move info:
   *                       {v, h, targetSquare, queen} v - distance for moving vertical (minus up),
   *                       h - distance for move horizontal (minus left), targetSquare is a new square with
   *                       moved draught, queen is a draught has become the queen
   * @throws BoardServiceError
   */
  public Board move(Square selectedSquare, Square nextSquare, boolean checkAllowed, Board toMove) {
    Board previous = (Board) toMove.deepClone();

    BoardUtils.moveDraught(selectedSquare, nextSquare, checkAllowed, toMove);
    toMove.pushPreviousBoard(previous.getId());

    Utils.setRandomIdAndCreatedAt(toMove);
    toMove.setCursor(true);

    previous.setCursor(false);
//    previous.pushPreviousBoard(toMove.getNextBoard());

    boardDao.save(previous);
    boardDao.save(toMove);
    return toMove;
  }

  public void save(Board board) {
    boardDao.save(board);
  }

  public Board addDraught(Board currentBoard, String notation, Draught draught) {
    BoardUtils.addDraught(currentBoard, notation, draught);
    boardDao.save(currentBoard);
    return currentBoard;
  }

  public Optional<Board> undo(Board currentBoard) {
    String previousId = currentBoard.popPreviousBoard();
    if (StringUtils.isBlank(previousId)) {
      return Optional.empty();
    }
    boardDao.save(currentBoard);
    return findById(previousId).map(previousBoard -> {
      previousBoard.pushNextBoard(currentBoard.getId());
      boardDao.save(previousBoard);
      return previousBoard;
    });
  }

  public Optional<Board> redo(Board currentBoard) {
    String nextId = currentBoard.popNextBoard();
    if (StringUtils.isBlank(nextId)) {
      return Optional.empty();
    }
    boardDao.save(currentBoard);
    return findById(nextId).map(nextBoard -> {
      nextBoard.pushPreviousBoard(currentBoard.getId());
      boardDao.save(nextBoard);
      return nextBoard;
    });
  }

  private Board undoRedoBoardAction(Board currentBoard, Board nextBoard) {
    Utils.setRandomIdAndCreatedAt(nextBoard);
//    nextBoard.setPreviousBoards(currentBoard.getId());

    Square nextSquare = currentBoard.getNextSquare();
    currentBoard.setNextSquare(nextBoard.getPreviousSquare());
    currentBoard.setPreviousSquare(nextSquare);

//    currentBoard.setNextBoard(nextBoard.getId());
    nextBoard.setCursor(true);
    currentBoard.setCursor(false);
//    System.out.println(currentBoard.getPreviousBoards().values());
//    System.out.println(nextBoard.getPreviousBoards().values());
    boardDao.batchSave(nextBoard, currentBoard);
    highlight(nextBoard);
    return nextBoard;
  }
}
