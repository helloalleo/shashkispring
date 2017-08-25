package com.workingbit.board.service;

import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.board.dao.BoardDao;
import com.workingbit.boardmodule.BoardUtils;
import com.workingbit.boardmodule.move.MoveManager;
import com.workingbit.coremodule.domain.impl.Board;
import com.workingbit.coremodule.domain.impl.BoardContainer;
import com.workingbit.coremodule.domain.impl.Draught;
import com.workingbit.coremodule.domain.impl.Square;
import com.workingbit.coremodule.exception.BoardServiceException;
import com.workingbit.share.domain.impl.NewBoardRequest;
import kotlin.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.workingbit.board.common.EnumBaseKeys.*;
import static com.workingbit.board.common.EnumSearch.allowed;
import static com.workingbit.board.common.EnumSearch.beaten;

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

  public Board createBoard(NewBoardRequest newBoardRequest) {
    Board board = BoardUtils.INSTANCE.initBoard(newBoardRequest.getFillBoard(),
        newBoardRequest.getBlack(),
        newBoardRequest.getRules(),
        newBoardRequest.getSquareSize());
    boardDao.save(board);
    return board;
  }

  public PaginatedScanList<Board> findAll() {
    return boardDao.findAll();
  }

  public Board findById(String boardId) {
    return boardDao.findById(boardId);
  }

  public void delete(String boardId) {
    boardDao.delete(boardId);
  }
//
//  public void addDraught(BoardContainer board, Draught draught) {
//    Optional<Square> draughtOnBoard = board.getSquares()
//        .stream()
//        // find square by coords of draught
//        .filter(square -> square.getV() == draught.getV() && square.getH() == draught.getH())
//        .findFirst();
//    draughtOnBoard.ifPresent(square -> {
//      square.setDraught((Draught) draught);
//    });
//  }


  /**
   * @param moveTo map of {boardId: String, selectedSquare: Square, targetSquare: Square, allowed: List<Square>, beaten: List<Square>}
   * @return Move info:
   * {v, h, targetSquare, queen} v - distance for moving vertical (minus up),
   * h - distance for move horizontal (minus left), targetSquare is a new square with
   * moved draught, queen is a draught has become the queen
   * @throws BoardServiceException
   */
  public Map<String, Object> move(Map<String, Object> moveTo) throws BoardServiceException {
    Board board = findById((String) moveTo.get(boardId.name()));
    if (board == null) {
      throw new BoardServiceException("Board not found");
    }
    Square selected = mapper.convertValue(moveTo.get(selectedSquare.name()), Square.class);
    Square target = mapper.convertValue(moveTo.get(targetSquare.name()), Square.class);
    List<Square> allowedMoves = (List<Square>) moveTo.get(allowed.name());
    List<Draught> beatenMoves = (List<Draught>) moveTo.get(beaten.name());
    boolean isUndo = (boolean) moveTo.getOrDefault(undoMove.name(), false);
    try {
      // create move service
      MoveManager moveUtil = new MoveManager(board.getBoardContainer(), selected, target, allowedMoves, beatenMoves, isUndo);
      // do move should update board
      Pair<BoardContainer, Map<String, Object>> move = moveUtil.moveAndUpdateBoard();
      board.setBoardContainer(move.getFirst());
//      boardHistoryService.addBoardAndSave(board);
      boardDao.save(board);
      return move.getSecond();
    } catch (BoardServiceException e) {
      return null;
    }
  }

  public Map<String, Object> undo(String boardId) throws BoardServiceException {
    Map<String, Object> undoMove = boardHistoryService.undo(boardId);
    return move(undoMove);
  }

  public void save(Board board) {
    boardDao.save(board);
  }
}
