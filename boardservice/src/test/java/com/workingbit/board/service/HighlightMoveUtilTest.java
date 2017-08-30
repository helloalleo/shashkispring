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
    testCollection("d4,b4", highlight.get(), allowed);
  }

  @Test
  public void draught_one_beat() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c3"); // c3
    Square squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "d4"); // c3
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    testCollection("d4", highlight.get(), beaten);
    testCollection("e5", highlight.get(), allowed);
  }

  @Test
  public void draught_beat_sequence() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c3"); // c3
    Square squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "d4"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "d6"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "b6"); // c3
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    testCollection("d4,d6,b6", highlight.get(),beaten);
    testCollection("c7,e5,a5", highlight.get(),allowed);
  }

  @Test
  public void turk_stroke_for_queen() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "e1"); // c3
    square.getDraught().setQueen(true);
    Square squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "c3"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "b6"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "e7"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "e5"); // c3
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    testCollection("c3,b6,e7,e5", highlight.get(), beaten);
    testCollection("b4,f8,a5,c7,d8,f6,d4,f4,g3,h2", highlight.get(), allowed);
  }

  @Test
  public void turk_stroke_for_draught() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c1"); // c3
    Square squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "b2"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "b4"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "d4"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "d6"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "f6"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "f4"); // c3
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    testCollection("b2,b4,d4,d6,f6,f4", highlight.get(), beaten);
    testCollection("a3,c5,e3,g5,e7", highlight.get(),allowed);
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
    testCollection("c3,b6,e5", highlight.get(), beaten);
    testCollection("h2,g3,c7,f4,a5", highlight.get(), allowed);
  }

  @Test
  public void draught_two_beaten() throws BoardServiceException, ExecutionException, InterruptedException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c3"); // c3
    Square squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "d4"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "d6"); // c3
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    testCollection("d4,d6", highlight.get(), beaten);
    testCollection("c7,e5", highlight.get(), allowed);
  }

  @Test
  public void queen_moves_on_empty_desk() throws BoardServiceException, ExecutionException, InterruptedException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c3");
    square.getDraught().setQueen(true);
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    List<String> collect = ((List<Square>) highlight.get().get(allowed.name())).stream().map(ICoordinates::toNotation).collect(Collectors.toList());
    assertEquals("[d4, e5, f6, g7, h8, b2, a1, b4, a5, d2, e1]", collect.toString());
  }

  @Test
  public void queen_moves_with_beat() throws BoardServiceException, ExecutionException, InterruptedException {
    Board board = getBoard();
    Square square = getSquareByVHWithDraught(board.getCurrentBoard(), "c3");
    square.getDraught().setQueen(true);
    Square blackSquare = getSquareByVHWithBlackDraught(board.getCurrentBoard(), "e5");
    Optional<Map<String, Object>> highlight = HighlightMoveUtil.highlight(board, square);
    assertTrue(highlight.isPresent());
    List<String> collect = ((List<Square>) highlight.get().get(beaten.name())).stream().map(ICoordinates::toNotation).collect(Collectors.toList());
    testCollection("e5", highlight.get(), beaten);
    testCollection("f6,g7,h8", highlight.get(), allowed);
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

  protected EnumRules getRules() {
    return EnumRules.RUSSIAN;
  }

  private Square getSquareByVHWithDraught(BoardContainer currentBoard, String notation) throws BoardServiceException {
    return BoardUtils.addDraught(currentBoard, notation, false);
  }

  private Square getSquareByVHWithBlackDraught(BoardContainer currentBoard, String notation) throws BoardServiceException {
    return BoardUtils.addDraught(currentBoard, notation, true);
  }

  private void testCollection(String notations, Map<String, Object> highlight, EnumSearch key) {
    List<String> collection = ((List<Square>) highlight.get(key.name())).stream().map(ICoordinates::toNotation).collect(Collectors.toList());
    String[] notation = notations.split(",");
    Arrays.stream(notation).forEach(n -> {
      assertTrue(collection.toString(), collection.contains(n));
    });
    assertEquals(collection.toString(), notation.length, collection.size());
  }
}