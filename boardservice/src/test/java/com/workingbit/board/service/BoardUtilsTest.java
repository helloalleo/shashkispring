package com.workingbit.board.service;

import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.ICoordinates;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Square;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Aleksey Popryaduhin on 13:52 27/08/2017.
 */
public class BoardUtilsTest {

  @Test
  public void test_main_road() {
    List<Square> squareDouble1Array = BoardUtils.getSquareArray(0, 8, getSquareSize(), false);
    String notation = squareDouble1Array.stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("h8,g7,f6,e5,d4,c3,b2,a1", notation);
  }

  @Test
  public void test_double_diagonals_main() throws Exception {
    List<Square> squareDouble1Array = BoardUtils.getSquareArray(-1, 8, getSquareSize(), true);
    String notation = squareDouble1Array.stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("a7,b6,c5,d4,e3,f2,g1", notation);

    List<Square> squareDouble2Array = BoardUtils.getSquareArray(1, 8, getSquareSize(), true);
    notation = squareDouble2Array.stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("b8,c7,d6,e5,f4,g3,h2", notation);
  }

  @Test
  public void test_triple_diagonals_sub() throws Exception {
    List<Square> squareDouble1Array = BoardUtils.getSquareArray(2, 8, getSquareSize(), false);
    String notation = squareDouble1Array.stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("h6,g5,f4,e3,d2,c1", notation);

    List<Square> squareDouble2Array = BoardUtils.getSquareArray(-2, 8, getSquareSize(), false);
    notation = squareDouble2Array.stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("f8,e7,d6,c5,b4,a3", notation);
  }

  @Test
  public void test_main_diagonals() {
    List<List<Square>> mainDiagonals = BoardUtils.getDiagonals(8, getSquareSize(), true);
    String stringStream = mainDiagonals.stream().map(squares -> squares.stream().map(Square::toNotation).collect(Collectors.joining(","))).collect(Collectors.joining(";"));
    assertEquals("a3,b2,c1;a5,b4,c3,d2,e1;a7,b6,c5,d4,e3,f2,g1;b8,c7,d6,e5,f4,g3,h2;d8,e7,f6,g5,h4;f8,g7,h6", stringStream);
  }

  @Test
  public void test_sub_diagonals() {
    List<List<Square>> mainDiagonals = BoardUtils.getDiagonals(8, getSquareSize(), false);
    String stringStream = mainDiagonals.stream().map(squares -> squares.stream().map(Square::toNotation).collect(Collectors.joining(","))).collect(Collectors.joining(";"));
    assertEquals("b8,a7;d8,c7,b6,a5;f8,e7,d6,c5,b4,a3;h8,g7,f6,e5,d4,c3,b2,a1;h6,g5,f4,e3,d2,c1;h4,g3,f2,e1;h2,g1", stringStream);
  }

  @Test
  public void test_all_diagonals() {
//    List<List<Square>> diagonals = BoardUtils.getAllDiagonals(8, getSquareSize());
//    String stringStream = diagonals.stream().map(squares -> squares.stream().map(Square::toNotation).collect(Collectors.joining(","))).collect(Collectors.joining(";"));
//    assertEquals("a3,b2,c1;a5,b4,c3,d2,e1;a7,b6,c5,d4,e3,f2,g1;b8,c7,d6,e5,f4,g3,h2;d8,e7,f6,g5,h4;f8,g7,h6;b8,a7;d8,c7,b6,a5;f8,e7,d6,c5,b4,a3;h8,g7,f6,e5,d4,c3,b2,a1;h6,g5,f4,e3,d2,c1;h4,g3,f2,e1;h2,g1", stringStream);
  }

  @Test
  public void test_init_board() {
    BoardContainer boardContainer = BoardUtils.initBoard(true, false, EnumRules.RUSSIAN, getSquareSize());
//    Map<Integer, List<List<List<String>>>> collect = boardContainer.getWhiteDraughts().stream().map(draught -> draught.getDiagonals()).map(lists -> lists.stream().map(squares -> squares.stream().map(square -> square.toNotation()).collect(Collectors.toList())).collect(Collectors.toList())).collect(Collectors.groupingBy(o -> o.size()));
//    assertEquals("{1=[[[h8, g7, f6, e5, d4, c3, b2, a1]]], 2=[[[a3, b2, c1], [f8, e7, d6, c5, b4, a3]], [[a3, b2, c1], [h8, g7, f6, e5, d4, c3, b2, a1]], [[a3, b2, c1], [h6, g5, f4, e3, d2, c1]], [[a5, b4, c3, d2, e1], [h8, g7, f6, e5, d4, c3, b2, a1]], [[a5, b4, c3, d2, e1], [h6, g5, f4, e3, d2, c1]], [[a5, b4, c3, d2, e1], [h4, g3, f2, e1]], [[a7, b6, c5, d4, e3, f2, g1], [h6, g5, f4, e3, d2, c1]], [[a7, b6, c5, d4, e3, f2, g1], [h4, g3, f2, e1]], [[a7, b6, c5, d4, e3, f2, g1], [h2, g1]], [[b8, c7, d6, e5, f4, g3, h2], [h4, g3, f2, e1]], [[b8, c7, d6, e5, f4, g3, h2], [h2, g1]]]}", collect.toString());
//    collect = boardContainer.getBlackDraughts().stream().map(draught -> draught.getDiagonals()).map(lists -> lists.stream().map(squares -> squares.stream().map(square -> square.toNotation()).collect(Collectors.toList())).collect(Collectors.toList())).collect(Collectors.groupingBy(o -> o.size()));
//    assertEquals("{1=[[[h8, g7, f6, e5, d4, c3, b2, a1]]], 2=[[[a7, b6, c5, d4, e3, f2, g1], [b8, a7]], [[a7, b6, c5, d4, e3, f2, g1], [d8, c7, b6, a5]], [[b8, c7, d6, e5, f4, g3, h2], [b8, a7]], [[b8, c7, d6, e5, f4, g3, h2], [d8, c7, b6, a5]], [[b8, c7, d6, e5, f4, g3, h2], [f8, e7, d6, c5, b4, a3]], [[d8, e7, f6, g5, h4], [d8, c7, b6, a5]], [[d8, e7, f6, g5, h4], [f8, e7, d6, c5, b4, a3]], [[d8, e7, f6, g5, h4], [h8, g7, f6, e5, d4, c3, b2, a1]], [[f8, g7, h6], [f8, e7, d6, c5, b4, a3]], [[f8, g7, h6], [h8, g7, f6, e5, d4, c3, b2, a1]], [[f8, g7, h6], [h6, g5, f4, e3, d2, c1]]]}", collect.toString());
  }

  @Test
  public void add_draught_to_board() throws BoardServiceException {
    BoardContainer boardContainer = BoardUtils.initBoard(true, false, EnumRules.RUSSIAN, getSquareSize());
    Square square = BoardUtils.addDraught(boardContainer, "c3", true);
    square.getDiagonals().forEach(squares -> {
      int index = squares.indexOf(square);
      Square square1 = squares.get(index);
      assertNotNull(square1.getDraught());
    });
  }

  private int getSquareSize() {
    return 60;
  }

}