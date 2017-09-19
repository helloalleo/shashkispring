package com.workingbit.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.board.dao.BoardDao;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.board.model.BeatenAndAllowedSquareMap;
import com.workingbit.share.model.CreateBoardRequest;
import com.workingbit.share.model.EnumRules;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static com.workingbit.board.common.EnumBaseKeys.*;
import static com.workingbit.board.common.EnumSearch.allowed;
import static com.workingbit.board.common.EnumSearch.beaten;
import static com.workingbit.board.service.BoardUtils.*;

/**
 * Created by Aleksey Popryaduhin on 13:45 09/08/2017.
 */
@Service
public class BoardService {

  private final BoardDao boardDao;
  private final ObjectMapper mapper;
  private final BoardHistoryService boardHistoryService;

  @Autowired
  public BoardService(BoardDao boardDao,
                      ObjectMapper mapper,
                      BoardHistoryService boardHistoryService) {
    this.boardDao = boardDao;
    this.mapper = mapper;
    this.boardHistoryService = boardHistoryService;
  }

  public BoardContainer createBoard(CreateBoardRequest newBoardRequest) {
    BoardContainer boardContainer = initBoard(newBoardRequest.getFillBoard(), newBoardRequest.getBlack(),
        EnumRules.valueOf(newBoardRequest.getRules().name()), newBoardRequest.getSquareSize());
    save(boardContainer);
//    boardHistoryService.addBoardAndSave(board);
    return boardContainer;
  }

  public List<BoardContainer> findAll(Integer limit) {
    return boardDao.findAll(limit);
  }

  public Optional<BoardContainer> findById(String boardId) {
    return boardDao.findById(boardId)
        .map(this::initBoardContainer);
  }

  private BoardContainer initBoardContainer(BoardContainer boardContainer) {
    BoardContainer initBoard = initBoard(false, boardContainer.isBlack(), boardContainer.getRules(), boardContainer.getSquareSize());
    return boardContainer.init(initBoard);
  }

  public void delete(String boardId) {
    boardDao.delete(boardId);
  }

  public void addDraught(BoardContainer board, Draught draught) {
//    Optional<Square> draughtOnBoard = board.getSquares()
//        .stream()
//        // find square by coords of draught
//        .filter(square -> square.getV() == draught.getV() && square.getH() == draught.getH())
//        .findFirst();
//    draughtOnBoard.ifPresent(square -> {
//      square.setDraught((Draught) draught);
//    });
  }

  /**
   * @param boardId
   * @param toHighlight map of {boardId, selectedSquare}
   * @return map of {allowed, beaten}
   * @throws BoardServiceException
   */
  public BeatenAndAllowedSquareMap highlight(String boardId, Square toHighlight) throws BoardServiceException {
    return boardDao.findById(boardId).map(board -> {
      try {
        // remember selected square
//        board.setSelectedSquare(square);
        boardDao.save(board);
        // highlight moves for the selected square
        return HighlightMoveService.highlight(board, toHighlight);
      } catch (BoardServiceException | InterruptedException | ExecutionException e) {
        e.printStackTrace();
        return null;
      }
    }).orElseThrow(getBoardServiceExceptionSupplier("Unable to find allowed moves"));
  }

  /**
   * @param moveTo map of {boardId: String, selectedSquare: Square, targetSquare: Square, allowed: List<Square>, beaten: List<Square>}
   * @return Move info:
   * {v, h, targetSquare, queen} v - distance for moving vertical (minus up),
   * h - distance for move horizontal (minus left), targetSquare is a new square with
   * moved draught, queen is a draught has become the queen
   * @throws BoardServiceException
   */
  public Map<String, Object> move(Map<String, Object> moveTo) throws BoardServiceException {
    Optional<BoardContainer> boardOptional = findById((String) moveTo.get(boardId.name()));
    return boardOptional.map(board -> {
      Square selected = mapper.convertValue(moveTo.get(selectedSquare.name()), Square.class);
      Square target = mapper.convertValue(moveTo.get(targetSquare.name()), Square.class);
      List<Square> allowedMoves = mapList((List<Square>) moveTo.get(allowed.name()), mapper, Square.class, Square.class);
      List<Draught> beatenMoves = mapList((List<Draught>) moveTo.get(beaten.name()), mapper, Draught.class, Draught.class);
      boolean isUndo = (boolean) moveTo.getOrDefault(undoMove.name(), false);
      try {
        // create move service
        MoveUtil moveUtil = new MoveUtil(board, selected, target, allowedMoves, beatenMoves, isUndo);
        // do move should update board
        Pair<BoardContainer, Map<String, Object>> move = moveUtil.moveAndUpdateBoard();
//        boardHistoryService.addBoardAndSave(board);
        boardDao.save(board);
        return move.getRight();
      } catch (BoardServiceException e) {
        return null;
      }
    }).orElseThrow(getBoardServiceExceptionSupplier("Move not allowed"));
  }

//  public Map<String, Object> undo(String boardId) throws BoardServiceException {
//    Map<String, Object> undoMove = boardHistoryService.undo(boardId);
//    return move(undoMove);
//  }

  public void save(BoardContainer board) {
    boardDao.save(board);
  }
}
