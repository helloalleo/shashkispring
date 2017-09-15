package com.workingbit.board.service;

import com.workingbit.board.common.EnumSearch;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.board.model.BeatenAndAllowedSquareMap;
import com.workingbit.share.domain.ICoordinates;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Square;
import com.workingbit.share.model.EnumRules;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
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
public class HighlightMoveServiceTest {
  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void findAllowedMoves() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    BoardContainer board = getBoard();
    Square square = getSquareByVHWithDraught(board, "c3"); // c3
    BeatenAndAllowedSquareMap highlight = HighlightMoveService.highlight(board, square);
    testCollection("d4,b4", highlight, allowed);
  }

  @Test
  public void draught_one_beat() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    BoardContainer board = getBoard();
    Square square = getSquareByVHWithDraught(board, "c3"); // c3
    Square squareBlack = getSquareByVHWithBlackDraught(board, "d4"); // c3
    BeatenAndAllowedSquareMap highlight = HighlightMoveService.highlight(board, square);
    testCollection("d4", highlight, beaten);
    testCollection("e5", highlight, allowed);
  }

  @Test
  public void draught_beat_sequence() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    BoardContainer board = getBoard();
    Square square = getSquareByVHWithDraught(board, "c3"); // c3
    Square squareBlack = getSquareByVHWithBlackDraught(board, "d4"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board, "d6"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board, "b6"); // c3
    BeatenAndAllowedSquareMap highlight = HighlightMoveService.highlight(board, square);
    testCollection("d4,d6,b6", highlight, beaten);
    testCollection("c7,e5,a5", highlight, allowed);
  }

  @Test
  public void turk_stroke_for_queen() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    BoardContainer board = getBoard();
    Square square = getSquareByVHWithDraught(board, "e1"); // c3
    square.getDraught().setQueen(true);
    Square squareBlack = getSquareByVHWithBlackDraught(board, "c3"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board, "b6"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board, "e7"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board, "e5"); // c3
    BeatenAndAllowedSquareMap highlight = HighlightMoveService.highlight(board, square);
    testCollection("c3,b6,e7,e5", highlight, beaten);
    testCollection("b4,f8,a5,c7,d8,d4,f4,g3,h2,f6", highlight, allowed);
  }

  @Test
  public void turk_stroke_for_draught() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    BoardContainer board = getBoard();
    Square square = getSquareByVHWithDraught(board, "c1"); // c3
    Square squareBlack = getSquareByVHWithBlackDraught(board, "b2"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board, "b4"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board, "d4"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board, "d6"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board, "f6"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board, "f4"); // c3
    BeatenAndAllowedSquareMap highlight = HighlightMoveService.highlight(board, square);
    testCollection("b2,b4,d4,d6,f6,f4", highlight, beaten);
    testCollection("a3,c5,e3,g5,e7", highlight, allowed);
  }

  @Test
  public void queen_beats_sequence() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    BoardContainer board = getBoard();
    Square square = getSquareByVHWithDraught(board, "e1"); // c3
    square.getDraught().setQueen(true);
    Square squareBlack = getSquareByVHWithBlackDraught(board, "c3"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board, "b6"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board, "e5"); // c3
    BeatenAndAllowedSquareMap highlight = HighlightMoveService.highlight(board, square);
    testCollection("c3,b6,e5", highlight, beaten);
    testCollection("h2,g3,c7,f4,a5", highlight, allowed);
  }

  @Test
  public void queen_beats_sequence2() throws BoardServiceException, ExecutionException, InterruptedException, TimeoutException {
    BoardContainer board = getBoard();
    Square square = getSquareByVHWithDraught(board, "e1"); // c3
    square.getDraught().setQueen(true);
    Square squareBlack = getSquareByVHWithBlackDraught(board, "d2"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board, "b6"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board, "e7"); // c3
    BeatenAndAllowedSquareMap highlight = HighlightMoveService.highlight(board, square);
    testCollection("d2,b6,e7", highlight, beaten);
    testCollection("b4,a5,d8,f8,f6,g5,h4", highlight, allowed);
  }

  @Test
  public void draught_two_beaten() throws BoardServiceException, ExecutionException, InterruptedException {
    BoardContainer board = getBoard();
    Square square = getSquareByVHWithDraught(board, "c3"); // c3
    Square squareBlack = getSquareByVHWithBlackDraught(board, "d4"); // c3
    squareBlack = getSquareByVHWithBlackDraught(board, "d6"); // c3
    BeatenAndAllowedSquareMap highlight = HighlightMoveService.highlight(board, square);
    testCollection("d4,d6", highlight, beaten);
    testCollection("c7,e5", highlight, allowed);
  }

  @Test
  public void queen_moves_on_empty_desk() throws BoardServiceException, ExecutionException, InterruptedException {
    BoardContainer board = getBoard();
    Square square = getSquareByVHWithDraught(board, "c3");
    square.getDraught().setQueen(true);
    BeatenAndAllowedSquareMap highlight = HighlightMoveService.highlight(board, square);
    testCollection("d4,e5,f6,g7,h8,b2,a1,b4,a5,d2,e1", highlight, allowed);
  }

  @Test
  public void queen_moves_with_beat() throws BoardServiceException, ExecutionException, InterruptedException {
    BoardContainer board = getBoard();
    Square square = getSquareByVHWithDraught(board, "c3");
    square.getDraught().setQueen(true);
    Square blackSquare = getSquareByVHWithBlackDraught(board, "e5");
    BeatenAndAllowedSquareMap highlight = HighlightMoveService.highlight(board, square);
    testCollection("e5", highlight, beaten);
    testCollection("f6,g7,h8", highlight, allowed);
  }

  BoardContainer getBoard() {
    BoardContainer boardContainer = BoardUtils.initBoard(false, false, EnumRules.RUSSIAN, 60);
//    Board board = new Board(boardContainer, false, EnumRules.RUSSIAN, 60);
//    BoardContainer currentBoard = board;
//    Optional<Square> squareByVH = BoardUtils.findSquareByNotation(currentBoard, "c3"); // 5,2
//    Square selectedSquare = squareByVH.get();
//    Draught draught = new Draught(5, 2, getRules().getDimension());
//    selectedSquare.setDraught(draught);
//    currentBoard.setSelectedSquare(selectedSquare);
    return boardContainer;
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

  private void testCollection(String notations, BeatenAndAllowedSquareMap highlight, EnumSearch key) {
    List<Square> items;
    if (key.equals(EnumSearch.allowed)) {
      items = highlight.getDefault().getAllowed();
    } else {
      items = highlight.getDefault().getBeaten();
    }
    List<String> collection = items.stream().map(ICoordinates::toNotation).collect(Collectors.toList());
    String[] notation = notations.split(",");
    Arrays.stream(notation).forEach(n -> {
      assertTrue(collection.toString(), collection.contains(n));
    });
    assertEquals(collection.toString(), notation.length, collection.size());
  }
}