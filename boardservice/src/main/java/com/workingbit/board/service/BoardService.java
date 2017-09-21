package com.workingbit.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.board.dao.BoardDao;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.board.model.Boards;
import com.workingbit.board.model.Strings;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;
import com.workingbit.share.model.CreateBoardRequest;
import com.workingbit.share.model.MovesList;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.workingbit.board.common.EnumBaseKeys.*;
import static com.workingbit.board.common.EnumSearch.allowed;
import static com.workingbit.board.common.EnumSearch.beaten;
import static com.workingbit.board.service.BoardUtils.*;
import static com.workingbit.board.service.HighlightMoveService.getHighlightedMoves;

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
        newBoardRequest.getRules());
    save(boardContainer);
//    boardHistoryService.addBoardAndSave(board);
    return boardContainer;
  }

  public List<BoardContainer> findAll(Integer limit) {
    return boardDao.findAll(limit);
  }

  public Optional<BoardContainer> findById(String boardId) {
    return boardDao.findById(boardId)
        .map(BoardUtils::updateBoard);
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
   * @return map of {allowed, beaten}
   * @throws BoardServiceException
   */
  public BoardContainer highlight(BoardContainer boardHighlight) throws BoardServiceException {
    String boardId = boardHighlight.getId();
    Square toHighlight = boardHighlight.getSelectedSquare();
    return boardDao.findById(boardId).map(board -> {
        BoardContainer boardContainer = BoardUtils.updateBoard(board);
        Optional<Square> squareHighlight = BoardUtils.findSquareLink(boardContainer, toHighlight);
        return squareHighlight.map(square -> {
          try {
            square.setDraught(toHighlight.getDraught());
            boardContainer.setSelectedSquare(square);
            MovesList highlighted = getHighlightedMoves(square);
            return highlightBoard(boardContainer, highlighted);
          } catch (BoardServiceException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
          }
          return null;
        }).orElse(null);
    }).orElseThrow(getBoardServiceExceptionSupplier("Unable to highlight the board"));
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

  public Boards findByIds(Strings boardIds) {
    List<String> ids = new ArrayList<>(boardIds.size());
    ids.addAll(boardIds);
    List<BoardContainer> boardList = boardDao.findByIds(ids)
        .stream()
        .map(BoardUtils::updateBoard)
        .collect(Collectors.toList());
    Boards boards = new Boards();
    boards.addAll(boardList);
    return boards;
  }
}
