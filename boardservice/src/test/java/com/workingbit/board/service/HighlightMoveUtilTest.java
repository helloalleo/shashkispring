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

import java.util.Arrays;
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

  @Test
  public void findAllowedMoves() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c3"); // c3
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    String squares = ((List<Square>) highlight.get().get(allowed.name())).stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("d4,b4", squares);
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
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "b6"); // c3
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    List<String> allowedMoves = ((List<Square>) highlight.get().get(allowed.name())).stream().map(ICoordinates::toNotation).collect(Collectors.toList());
    List<String> beatenDraughts = ((List<Square>) highlight.get().get(beaten.name())).stream().map(ICoordinates::toNotation).collect(Collectors.toList());
    Arrays.stream("d4,d6,b6".split(",")).forEach(n -> {
      assertTrue(beatenDraughts.toString(), beatenDraughts.contains(n));
    });
    assertEquals(3, beatenDraughts.size());
    Arrays.stream("c7,e5,a5".split(",")).forEach(n -> {
      assertTrue(allowedMoves.toString(), allowedMoves.contains(n));
    });
    assertEquals(3, allowedMoves.size());
  }

  @Test
  public void turk_stroke() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "e1"); // c3
    square.getDraught().setQueen(true);
    Square squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "c3"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "b6"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "e7"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "e5"); // c3
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    List<String> allowedMoves = ((List<Square>) highlight.get().get(allowed.name())).stream().map(ICoordinates::toNotation).collect(Collectors.toList());
    List<String> beatenDraughts = ((List<Square>) highlight.get().get(beaten.name())).stream().map(ICoordinates::toNotation).collect(Collectors.toList());
    Arrays.stream("c3,b6,e7,e5".split(",")).forEach(n -> {
      assertTrue(beatenDraughts.toString(), beatenDraughts.contains(n));
    });
    assertEquals(4, beatenDraughts.size());
    Arrays.stream("f8,b4,a5,f4,g3,h2,c7,d8,d4".split(",")).forEach(n -> {
      assertTrue(allowedMoves.contains(n));
    });
    System.out.println(allowedMoves);
    assertEquals(9, allowedMoves.size());
  }

  @Test
  public void queen_beats_sequence() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "e1"); // c3
    square.getDraught().setQueen(true);
    Square squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "c3"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "b6"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "e5"); // c3
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    List<String> allowedMoves = ((List<Square>) highlight.get().get(allowed.name())).stream().map(ICoordinates::toNotation).collect(Collectors.toList());
    List<String> beatenDraughts = ((List<Square>) highlight.get().get(beaten.name())).stream().map(ICoordinates::toNotation).collect(Collectors.toList());
    Arrays.stream("c3,b6,e5".split(",")).forEach(n -> {
      assertTrue(beatenDraughts.toString(), beatenDraughts.contains(n));
    });
    assertEquals(3, beatenDraughts.size());
    Arrays.stream("b4,a5,f4,g3,h2,c7,d8".split(",")).forEach(n -> {
      assertTrue(allowedMoves.contains(n));
    });
    assertEquals(7, allowedMoves.size());
  }

  @Test
  public void find_two_beaten_forward() throws BoardServiceException, ExecutionException, InterruptedException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c3"); // c3
    Square squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "d4"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "d6"); // c3
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    String allowedMoves = ((List<Square>) highlight.get().get(allowed.name())).stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    String beatenDraughts = ((List<Square>) highlight.get().get(beaten.name())).stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("c7,e5", allowedMoves);
    assertEquals("d4,d6", beatenDraughts);
  }

  @Test
  public void find_queen_moves_on_empty_desk() throws BoardServiceException, ExecutionException, InterruptedException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c3");
    square.getDraught().setQueen(true);
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    List<String> collect = ((List<Square>) highlight.get().get(allowed.name())).stream().map(ICoordinates::toNotation).collect(Collectors.toList());
    assertEquals("[d4, e5, f6, g7, h8, b2, a1, b4, a5, d2, e1]", collect.toString());
  }

  @Test
  public void find_queen_moves_with_beat() throws BoardServiceException, ExecutionException, InterruptedException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c3");
    square.getDraught().setQueen(true);
    Square blackSquare = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "e5");
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    List<String> collect = ((List<Square>) highlight.get().get(beaten.name())).stream().map(ICoordinates::toNotation).collect(Collectors.toList());
    assertEquals("[e5]", collect.toString());
    collect = ((List<Square>) highlight.get().get(allowed.name())).stream().map(ICoordinates::toNotation).collect(Collectors.toList());
    assertEquals("[f6, g7, h8]", collect.toString());
  }

//  @Test
//  public void find_turk_stroke() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
//    Board board = getBoard();
//    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c3"); // c3
//    Square squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "d4"); // c3
//    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "d6"); // c3
//    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
//    assertTrue(highlight.isPresent());
//    String allowedMoves = ((List<Square>) highlight.get().get(allowed.name())).stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
//    String beatenDraughts = ((List<Square>) highlight.get().get(beaten.name())).stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
//    assertEquals("e5,c7", allowedMoves);
//    assertEquals("d4,d6", beatenDraughts);
//  }

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
    Optional<Square> squareByVH = BoardUtils.findSquareByNotation(currentBoard, "c3"); // 5,2
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

  private Square getSquareByVHWithDraught(BoardContainer currentBoard, String notation) throws BoardServiceException {
    return BoardUtils.addDraught(currentBoard, notation, false);
  }

  private Square getSquareByVHWithBlackDraught(BoardContainer currentBoard, String notation) throws BoardServiceException {
    return BoardUtils.addDraught(currentBoard, notation, true);
  }

}