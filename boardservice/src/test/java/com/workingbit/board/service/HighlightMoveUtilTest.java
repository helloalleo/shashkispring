package com.workingbit.board.service;

import com.workingbit.board.common.EnumSearch;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.ICoordinates;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.workingbit.board.common.EnumSearch.allowed;
import static com.workingbit.board.common.EnumSearch.beaten;
import static com.workingbit.board.service.BoardUtils.findSquareByNotation;
import static com.workingbit.board.service.BoardUtils.findSquareByVH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Aleksey Popryaduhin on 20:01 10/08/2017.
 */
public class HighlightMoveUtilTest {
  @Before
  public void setUp() throws Exception {
  }

  /* this was commented because it was intermediate
  @Test
  public void filterNotOnMainAndSelectedSquares() throws Exception, BoardServiceException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c3");
    HighlightMoveUtil highlight = new HighlightMoveUtil(board.getCurrentBoard(), square, board.getRules());
    CompletableFuture<List<Square>> x = highlight.filterNotOnMainAndSelectedSquares();
    List<Square> squareStream = x.get();
    String squares = squareStream.stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("h8,g7,f6,a5,e5,b4,d4,b2,d2,a1,e1", squares);
  }

  @Test
  public void filterQueenSquares() throws BoardServiceException, ExecutionException, InterruptedException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c3"); // c3
    HighlightMoveUtil highlight = new HighlightMoveUtil(board.getCurrentBoard(), square, board.getRules());
    CompletableFuture<List<Square>> x = highlight.filterQueenSquares();
    List<Square> squareStream = x.get();
    String squares = squareStream.stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("a5,e5,b4,d4,b2,d2,a1,e1", squares);
  }
  */

  @Test
  public void findAllowedMoves() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c3"); // c3
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    String squares = ((List<Square>) highlight.get().get(allowed.name())).stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("b4,d4", squares);
  }

  @Test
  public void find_one_beaten_allowed_move() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c3"); // c3
    Square squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "d4"); // c3
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    String allowedMoves = ((List<Square>) highlight.get().get(allowed.name())).stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    String beatenDraughts = ((List<Square>) highlight.get().get(beaten.name())).stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("e5", allowedMoves);
    assertEquals("d4", beatenDraughts);
  }

  @Test
  public void find_sequence_beaten_and_allowed_move() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c3"); // c3
    Square squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "d4"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "d6"); // c3
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    String allowedMoves = ((List<Square>) highlight.get().get(allowed.name())).stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    String beatenDraughts = ((List<Square>) highlight.get().get(beaten.name())).stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("c7,e5", allowedMoves);
    assertEquals("d6,d4", beatenDraughts);
  }

//  @Test
//  public void shouldBlackDraughtMoveBackwardOnOnePosition() throws Exception, BoardServiceException {
//    Board board = getBoard();
//    Draught draught = getDraughtBlack(5, 2);
//    Square square = getSquare(draught, 5, 2);
//    HighlightMoveUtil highlightMoveUtil = new HighlightMoveUtil(board.getCurrentBoard(), (Square) square, getRules());
//    Map<String, Object> allowedMoves = highlightMoveUtil.findAllMoves();
//    assertTrue(allowedMoves.size() > 0);
//    assertEquals("(6,1)(6,3)", resultToString(allowedMoves, allowed));
//  }

//  @Test
//  public void shouldWhiteDraughtBeatForward() throws Exception, BoardServiceException {
//    Board board = getBoard();
//    BoardService boardService = getBoardService();
//    // add black draught
//    boardService.addDraught(board.getCurrentBoard(), getDraughtBlack(4, 3));
//    Draught draught = getDraught(5, 2);
//    // find square on board
//    Square square = getSquareByVH(board.getCurrentBoard(), 5, 2);
//    // set draught for square
//    square.setDraught((Draught) draught);
//    HighlightMoveUtil highlightMoveUtil = new HighlightMoveUtil(board.getCurrentBoard(), (Square) square, getRules());
//    Map<String, Object> allowedMoves = highlightMoveUtil.findAllMoves();
//    assertTrue(allowedMoves.size() > 0);
//    assertEquals("(3,4)", resultToString(allowedMoves, allowed));
//    assertEquals("(4,3)", resultToString(allowedMoves, beaten));
//  }

//  @Test
//  public void shouldWhiteDraughtBeatForwardTwice() throws Exception, BoardServiceException {
//    Board board = getBoard();
//    BoardService boardService = getBoardService();
//    boardService.addDraught(board.getCurrentBoard(), getDraughtBlack(4, 3));
//    boardService.addDraught(board.getCurrentBoard(), getDraughtBlack(4, 1));
//    Draught draught = getDraught(5, 2);
//    Square square = getSquareByVH(board.getCurrentBoard(), 5, 2);
//    square.setDraught((Draught) draught);
//    HighlightMoveUtil highlightMoveUtil = new HighlightMoveUtil(board.getCurrentBoard(), (Square) square, getRules());
//    Map<String, Object> allowedMoves = highlightMoveUtil.findAllMoves();
//    assertTrue(allowedMoves.size() > 0);
//    assertEquals("(3,0)(3,4)", resultToString(allowedMoves, allowed));
//    assertEquals("(4,1)(4,3)", resultToString(allowedMoves, beaten));
//  }

//  @Test
//  public void shouldWhiteDraughtBeatBackward() throws Exception, BoardServiceException {
//    Board board = getBoard();
//    BoardService boardService = getBoardService();
//    boardService.addDraught(board.getCurrentBoard(), getDraughtBlack(6, 1));
//    Draught draught = getDraught(5, 2);
//    Square square = getSquareByVH(board.getCurrentBoard(), 5, 2);
//    square.setDraught((Draught) draught);
//    HighlightMoveUtil highlightMoveUtil = new HighlightMoveUtil(board.getCurrentBoard(), (Square) square, getRules());
//    Map<String, Object> allowedMoves = highlightMoveUtil.findAllMoves();
//    assertTrue(allowedMoves.size() > 0);
//    assertEquals("(7,0)", resultToString(allowedMoves, allowed));
//    assertEquals("(6,1)", resultToString(allowedMoves, beaten));
//  }

  //  @Test
//  public void shouldWhiteDraughtBeatBackwardTwice() throws Exception, BoardServiceException {
//    Board board = getBoard();
//    BoardService boardService = getBoardService();
//    boardService.addDraught(board.getCurrentBoard(), getDraughtBlack(6, 1));
//    boardService.addDraught(board.getCurrentBoard(), getDraughtBlack(6, 3));
//    Draught draught = getDraught(5, 2);
//    Square square = getSquareByVH(board.getCurrentBoard(), 5, 2);
//    square.setDraught((Draught) draught);
//    HighlightMoveUtil highlightMoveUtil = new HighlightMoveUtil(board.getCurrentBoard(), (Square) square, getRules());
//    Map<String, Object> allowedMoves = highlightMoveUtil.findAllMoves();
//    assertTrue(allowedMoves.size() > 0);
//    assertEquals("(7,0)(7,4)", resultToString(allowedMoves, allowed));
//    assertEquals("(6,1)(6,3)", resultToString(allowedMoves, beaten));
//  }
//
  private String resultToString(Map<String, Object> allowedMoves, EnumSearch enumSearch) {
    return ((List<ICoordinates>) allowedMoves.get(enumSearch.name()))
        .stream()
        .map(s -> String.format("(%s,%s)", s.getV(), s.getH()))
        .collect(Collectors.joining());
  }

  Board getBoard() {
    BoardContainer boardContainer = BoardUtils.initBoard(false, false, EnumRules.RUSSIAN, 60);
    Board board = new Board(boardContainer, false, EnumRules.RUSSIAN, 60);
    BoardContainer currentBoard = board.getCurrentBoard();
    Optional<Square> squareByVH = BoardUtils.findSquareByVH(currentBoard, 5, 2);
    Square selectedSquare = squareByVH.get();
    Draught draught = new Draught(5, 2, getRules().getDimension());
    selectedSquare.setDraught(draught);
    currentBoard.setSelectedSquare(selectedSquare);
    return board;
  }

  Draught getDraught(int v, int h) {
    return new Draught(v, h, getRules().getDimension());
  }

  Square getSquare(Draught draught, int v, int h) {
    return new Square(v, h, getRules().getDimension(), true, 60, draught);
  }

  Draught getDraughtBlack(int v, int h) {
    return new Draught(v, h, getRules().getDimension(), true);
  }

  Square getSquareByVH(BoardContainer board, int v, int h) {
    return findSquareByVH(board, v, h).get();
  }

  Square getSquareByNotation(BoardContainer boardContainer, String notation) {
    return findSquareByNotation(boardContainer, notation).get();
  }

  protected EnumRules getRules() {
    return EnumRules.RUSSIAN;
  }

  private Square getSquareByVHWithDraught(BoardContainer currentBoard, String notation) {
    Square square = getSquareByNotation(currentBoard, notation);
    Draught draught = getDraught(square.getV(), square.getH());
    square.setDraught(draught);
    return square;
  }

  private Square getSquareByVHWithBlackDraught(BoardContainer currentBoard, String notation) {
    Square square = getSquareByNotation(currentBoard, notation);
    Draught draught = getDraughtBlack(square.getV(), square.getH());
    square.setDraught(draught);
    return square;
  }

}