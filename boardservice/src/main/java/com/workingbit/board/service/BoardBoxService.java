package com.workingbit.board.service;

import com.workingbit.board.dao.BoardBoxDao;
import com.workingbit.board.model.BoardBoxes;
import com.workingbit.board.model.Strings;
import com.workingbit.share.common.Log;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.BoardBox;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;
import com.workingbit.share.model.CreateBoardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.workingbit.board.common.AppConstants.INTERNAL_SERVER_ERROR;
import static com.workingbit.board.service.BoardUtils.getBoardServiceExceptionSupplier;

/**
 * Created by Aleksey Popryaduhin on 07:00 22/09/2017.
 */
@Service
public class BoardBoxService {

  private final BoardBoxDao boardBoxDao;
  private final BoardService boardService;

  @Autowired
  public BoardBoxService(BoardBoxDao boardBoxDao, BoardService boardService) {
    this.boardBoxDao = boardBoxDao;
    this.boardService = boardService;
  }

  public BoardBox createBoard(CreateBoardRequest createBoardRequest) {
    Board board = boardService.createBoard(createBoardRequest);

    BoardBox boardBox = new BoardBox(board);
    boardBox.setArticleId(createBoardRequest.getArticleId());
    boardBox.setId(createBoardRequest.getBoardBoxId());
    boardBox.setCreatedAt(new Date());
    save(boardBox);

    board.setBoardBoxId(boardBox.getId());
    boardService.save(board);
    return boardBox;
  }

  public Optional<BoardBox> findById(String boardBoxId) {
    return boardBoxDao.findByKey(boardBoxId).map(this::updateBoardBox);
  }

  private BoardBox updateBoardBox(BoardBox boardBox) {
    Optional<Board> boardOptional = boardService.findById(boardBox.getBoardId());
    return boardOptional.map(board -> {
      boardBox.setBoard(board);
      return boardBox;
    }).orElse(null);
  }

  public void delete(String boardBoxId) {
    boardBoxDao.findById(boardBoxId)
        .map(boardBox -> {
          boardService.delete(boardBox.getBoardId());
          boardBoxDao.delete(boardBox.getId());
          return null;
        });
  }

  public Optional<BoardBox> highlight(BoardBox boardBox) {
    return findById(boardBox.getId())
        .map(updated -> {
          Board currentBoard = updated.getBoard();
          Board updatedBoard = BoardUtils.updateBoard(currentBoard);
          BoardUtils.updateMoveSquaresHighlight(updatedBoard, boardBox.getBoard());
          updatedBoard = boardService.highlight(updatedBoard);
          updated.setBoard(updatedBoard);
          return updated;
        });
  }

  public Optional<BoardBox> move(BoardBox boardBox) {
    return findById(boardBox.getId())
        .map(updatedBox -> {
          Board boardUpdated = updatedBox.getBoard();
          BoardUtils.updateMoveSquaresHighlight(boardUpdated, boardBox.getBoard());
          Square nextSquare = boardUpdated.getNextSquare();
          Square selectedSquare = boardUpdated.getSelectedSquare();
          if (isValidMove(nextSquare, selectedSquare)) {
            Log.error(String.format("Invalid move Next: %s, Selected: %s", nextSquare, selectedSquare));
            return null;
          }
          boardUpdated = boardService.move(boardUpdated, selectedSquare, nextSquare);
          updatedBox.setBoard(boardUpdated);
          updatedBox.setBoardId(boardUpdated.getId());
          boardBoxDao.save(updatedBox);
          return updatedBox;
        });
  }

  private boolean isValidMove(Square nextSquare, Square selectedSquare) {
    return nextSquare == null
        || selectedSquare == null
        || !selectedSquare.isOccupied()
        || !nextSquare.isHighlighted();
  }

  private void save(BoardBox boardBox) {
    boardBoxDao.save(boardBox);
  }

  public BoardBoxes findByIds(Strings boardIds) {
    List<String> ids = new ArrayList<>(boardIds.size());
    ids.addAll(boardIds);
    List<BoardBox> boardBoxList = boardBoxDao.findByIds(ids)
        .stream()
        .map(this::updateBoardBox)
        .collect(Collectors.toList());
    BoardBoxes boardBoxs = new BoardBoxes();
    boardBoxs.addAll(boardBoxList);
    return boardBoxs;
  }

  public Optional<BoardBox> addDraught(BoardBox boardBox) {
    Square selectedSquare = boardBox.getSelectedSquare();
    if (selectedSquare == null
        || !selectedSquare.isOccupied()) {
      return Optional.empty();
    }
    Draught draught = selectedSquare.getDraught();
    return findById(boardBox.getId())
        .map(updated -> {
          Board currentBoard = updated.getBoard();
          Square squareLink = BoardUtils.findSquareLink(currentBoard, selectedSquare)
              .orElseThrow(getBoardServiceExceptionSupplier(INTERNAL_SERVER_ERROR));
          currentBoard = boardService.addDraught(currentBoard, squareLink.getNotation(), draught);
          if (currentBoard == null) {
            return null;
          }
          updated.setBoard(currentBoard);
          return updated;
        });
  }

  public Optional<BoardBox> undo(BoardBox boardBox) {
    return findById(boardBox.getId())
        .map(updated -> {
          Board currentBoard = updated.getBoard();
          BoardUtils.updateMoveSquaresHighlight(currentBoard, boardBox.getBoard());
          Optional<Board> undone = boardService.undo(currentBoard);
          if (undoRedoBoardAction(updated, undone)) return updated;
          return updated;
        });
  }

  public Optional<BoardBox> redo(BoardBox boardBox) {
    return findById(boardBox.getId())
        .map(updated -> {
          Board currentBoard = updated.getBoard();
          BoardUtils.updateMoveSquaresHighlight(currentBoard, boardBox.getBoard());
          Optional<Board> redone = boardService.redo(currentBoard);
          if (undoRedoBoardAction(updated, redone)) return updated;
          return updated;
        });
  }

  private boolean undoRedoBoardAction(BoardBox updated, Optional<Board> redone) {
    if (redone.isPresent()) {
      updated.setBoard(redone.get());
      updated.setBoardId(redone.get().getId());
      boardBoxDao.save(updated);
      return true;
    }
    return false;
  }
}
