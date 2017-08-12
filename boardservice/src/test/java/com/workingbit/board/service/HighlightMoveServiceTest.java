package com.workingbit.board.service;

import com.workingbit.board.common.EnumSearch;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.domain.IBoard;
import com.workingbit.share.domain.ICoordinates;
import com.workingbit.share.domain.IDraught;
import com.workingbit.share.domain.ISquare;
import com.workingbit.share.domain.impl.Draught;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.workingbit.board.common.EnumSearch.allowed;
import static com.workingbit.board.common.EnumSearch.beaten;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Aleksey Popryaduhin on 20:01 10/08/2017.
 */
public class HighlightMoveServiceTest extends BaseServiceTest {

  @Test
  public void shouldWhiteDraughtMoveForwardOnOnePosition() throws Exception, BoardServiceException {
    IBoard board = getBoard();
    IDraught draught = getDraught(5, 2);
    ISquare square = getSquare((Draught) draught, 5, 2);
    HighlightMoveService highlightMoveService = new HighlightMoveService(board, square);
    Map<String, Object> allowedMoves = highlightMoveService.findAllowedMoves();
    assertTrue(allowedMoves.size() > 0);
    assertEquals("(4,1)(4,3)", resultToString(allowedMoves, allowed));
  }

  @Test
  public void shouldBlackDraughtMoveBackwardOnOnePosition() throws Exception, BoardServiceException {
    IBoard board = getBoard();
    Draught draught = getDraughtBlack(5, 2);
    ISquare square = getSquare(draught, 5, 2);
    HighlightMoveService highlightMoveService = new HighlightMoveService(board, square);
    Map<String, Object> allowedMoves = highlightMoveService.findAllowedMoves();
    assertTrue(allowedMoves.size() > 0);
    assertEquals("(6,1)(6,3)", resultToString(allowedMoves, allowed));
  }

  @Test
  public void shouldWhiteDraughtBeatForward() throws Exception, BoardServiceException {
    IBoard board = getBoard();
    BoardService boardService = getBoardService();
    // add black draught
    boardService.addDraught(board, getDraughtBlack(4, 3));
    IDraught draught = getDraught(5, 2);
    // find square on board
    ISquare square = getSquareByVH(board, 5, 2);
    // set draught for square
    square.setDraught((Draught) draught);
    HighlightMoveService highlightMoveService = new HighlightMoveService(board, square);
    Map<String, Object> allowedMoves = highlightMoveService.findAllowedMoves();
    assertTrue(allowedMoves.size() > 0);
    assertEquals("(3,4)", resultToString(allowedMoves, allowed));
    assertEquals("(4,3)", resultToString(allowedMoves, beaten));
  }

  @Test
  public void shouldWhiteDraughtBeatForwardTwice() throws Exception, BoardServiceException {
    IBoard board = getBoard();
    BoardService boardService = getBoardService();
    boardService.addDraught(board, getDraughtBlack(4, 3));
    boardService.addDraught(board, getDraughtBlack(4, 1));
    IDraught draught = getDraught(5, 2);
    ISquare square = getSquareByVH(board, 5, 2);
    square.setDraught((Draught) draught);
    HighlightMoveService highlightMoveService = new HighlightMoveService(board, square);
    Map<String, Object> allowedMoves = highlightMoveService.findAllowedMoves();
    assertTrue(allowedMoves.size() > 0);
    assertEquals("(3,0)(3,4)", resultToString(allowedMoves, allowed));
    assertEquals("(4,1)(4,3)", resultToString(allowedMoves, beaten));
  }

  @Test
  public void shouldWhiteDraughtBeatBackward() throws Exception, BoardServiceException {
    IBoard board = getBoard();
    BoardService boardService = getBoardService();
    boardService.addDraught(board, getDraughtBlack(6, 1));
    IDraught draught = getDraught(5, 2);
    ISquare square = getSquareByVH(board, 5, 2);
    square.setDraught((Draught) draught);
    HighlightMoveService highlightMoveService = new HighlightMoveService(board, square);
    Map<String, Object> allowedMoves = highlightMoveService.findAllowedMoves();
    assertTrue(allowedMoves.size() > 0);
    assertEquals("(7,0)", resultToString(allowedMoves, allowed));
    assertEquals("(6,1)", resultToString(allowedMoves, beaten));
  }

  @Test
  public void shouldWhiteDraughtBeatBackwardTwice() throws Exception, BoardServiceException {
    IBoard board = getBoard();
    BoardService boardService = getBoardService();
    boardService.addDraught(board, getDraughtBlack(6, 1));
    boardService.addDraught(board, getDraughtBlack(6, 3));
    IDraught draught = getDraught(5, 2);
    ISquare square = getSquareByVH(board, 5, 2);
    square.setDraught((Draught) draught);
    HighlightMoveService highlightMoveService = new HighlightMoveService(board, square);
    Map<String, Object> allowedMoves = highlightMoveService.findAllowedMoves();
    assertTrue(allowedMoves.size() > 0);
    assertEquals("(7,0)(7,4)", resultToString(allowedMoves, allowed));
    assertEquals("(6,1)(6,3)", resultToString(allowedMoves, beaten));
  }

  private String resultToString(Map<String, Object> allowedMoves, EnumSearch enumSearch) {
    return ((List<ICoordinates>) allowedMoves.get(enumSearch.name()))
        .stream()
        .map(s -> String.format("(%s,%s)", s.getV(), s.getH()))
        .collect(Collectors.joining());
  }
}