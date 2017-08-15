package com.workingbit.board.service;

import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.board.dao.BoardDao;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.impl.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.workingbit.board.common.EnumBaseKeys.*;
import static com.workingbit.board.common.EnumSearch.allowed;
import static com.workingbit.board.common.EnumSearch.beaten;
import static com.workingbit.board.service.BoardUtils.getBoardServiceExceptionSupplier;
import static com.workingbit.board.service.BoardUtils.mapList;

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
    Board board = initBoard(newBoardRequest.isFillBoard(), newBoardRequest.isBlack(), newBoardRequest.getRules(), newBoardRequest.getSquareSize());
    boardDao.save(board);
    return board;
  }

  public PaginatedScanList<Board> findAll() {
    return boardDao.findAll();
  }

  public Optional<Board> findById(String boardId) {
    return boardDao.findById(boardId);
  }

  public void delete(String boardId) {
    boardDao.delete(boardId);
  }

  /**
   * Fill board with draughts
   *
   * @param fillBoard
   * @param black      is player plays black?
   * @param rules
   * @param squareSize size of one square
   * @return
   */
  private Board initBoard(boolean fillBoard, boolean black, EnumRules rules, Integer squareSize) {
    List<Square> squares = new ArrayList<>();
    List<Draught> whiteDraughts = new ArrayList<>();
    List<Draught> blackDraughts = new ArrayList<>();
    for (int v = 0; v < rules.getDimension(); v++) {
      for (int h = 0; h < rules.getDimension(); h++) {
        Draught draught = new Draught(v, h, true);
        boolean draughtAdded = false;
        if (fillBoard && ((h + v + 1) % 2 == 0)) {
          if (v < rules.getRowsForDraughts()) {
            draught.setBlack(!black);
            draughtAdded = true;
          } else if (v >= rules.getDimension() - rules.getRowsForDraughts() && v < rules.getDimension()) {
            draught.setBlack(black);
            draughtAdded = true;
          }
        }
        if (draughtAdded) {
          if (draught.isBlack()) {
            blackDraughts.add(draught);
          } else {
            whiteDraughts.add(draught);
          }
        } else {
          draught = null;
        }
        Square square = new Square(v, h, (h + v + 1) % 2 == 0, squareSize, draught);
        squares.add(square);
      }
    }
    BoardContainer boardChanger = new BoardContainer(squares, whiteDraughts, blackDraughts, null);
    Board board = new Board(boardChanger, black, rules, squareSize);
    boardHistoryService.addBoardAndSave(board);
    return board;
  }

  public void addDraught(BoardContainer board, Draught draught) {
    Optional<Square> draughtOnBoard = board.getSquares()
        .stream()
        // find square by coords of draught
        .filter(square -> square.getV() == draught.getV() && square.getH() == draught.getH())
        .findFirst();
    draughtOnBoard.ifPresent(square -> {
      square.setDraught((Draught) draught);
    });
  }

  /**
   *
   * @param highlightFor map of {boardId, selectedSquare}
   * @return map of {allowed, beaten}
   * @throws BoardServiceException
   */
  public Map<String, Object> highlight(Map<String, Object> highlightFor) throws BoardServiceException {
    String aBoardId = (String) highlightFor.get(boardId.name());
    return boardDao.findById(aBoardId).map(board -> {
      try {
        Square square = mapper.convertValue(highlightFor.get(selectedSquare.name()), Square.class);
        // remember selected square
        board.getCurrentBoard().setSelectedSquare(square);
        boardDao.save(board);
        // highlight moves for the selected square
        HighlightMoveUtil highlightMoveUtil = HighlightMoveUtil.getService(board.getCurrentBoard(), square, board.getRules());
        return highlightMoveUtil.findAllowedMoves();
      } catch (BoardServiceException e) {
        return null;
      }
    }).orElseThrow(getBoardServiceExceptionSupplier("Unable to find allowed moves"));
  }

  /**
   * @param moveTo map of {boardId: String, selectedSquare: Square, targetSquare: Square, allowed: List<Square>, beaten: List<Square>}
   * @return Move info:
   *        {v, h, targetSquare, queen} v - distance for moving vertical (minus up),
   *        h - distance for move horizontal (minus left), targetSquare is a new square with
   *        moved draught, queen is a draught has become the queen
   * @throws BoardServiceException
   */
  public Map<String, Object> move(Map<String, Object> moveTo) throws BoardServiceException {
    Optional<Board> boardOptional = findById((String) moveTo.get(boardId.name()));
    return boardOptional.map(board -> {
      Square selected = mapper.convertValue(moveTo.get(selectedSquare.name()), Square.class);
      Square target = mapper.convertValue(moveTo.get(targetSquare.name()), Square.class);
      List<Square> allowedMoves = mapList((List<Square>) moveTo.get(allowed.name()), mapper, Square.class, Square.class);
      List<Draught> beatenMoves = mapList((List<Draught>) moveTo.get(beaten.name()), mapper, Draught.class, Draught.class);
      try {
        // create move service
        MoveUtil moveUtil = MoveUtil.getService(board.getCurrentBoard(), selected, target, allowedMoves, beatenMoves);
        // do move should update board
        Pair<BoardContainer, Map<String, Object>> move = moveUtil.moveAndUpdateBoard();
        board.setCurrentBoard(move.getLeft());
        boardHistoryService.addBoardAndSave(board);
        boardDao.save(board);
        return move.getRight();
      } catch (BoardServiceException e) {
        return null;
      }
    }).orElseThrow(getBoardServiceExceptionSupplier("Move not allowed"));
  }

  public Map<String, Object> undo(String boardId) throws BoardServiceException {
    Map<String, Object> undoMove = boardHistoryService.undo(boardId);
    return move(undoMove);
  }

  public void save(Board board) {
    boardDao.save(board);
  }
}
