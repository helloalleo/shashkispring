package com.workingbit.board.service;

import com.workingbit.share.domain.ICoordinates;
import com.workingbit.share.domain.impl.Square;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Created by Aleksey Popryaduhin on 13:52 27/08/2017.
 */
public class BoardUtilsTest {

  @Test
  public void test_main_road() {
    List<Square> squareDouble1Array = BoardUtils.getSquareArray(0, 0, 8, 60, false);
    String notation = squareDouble1Array.stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("h8,g7,f6,e5,d4,c3,b2,a1", notation);
  }

  @Test
  public void get_double_diagonals_big() throws Exception {
    List<Square> squareDouble1Array = BoardUtils.getSquareArray(1, 0, 8, 60, true);
    String notation = squareDouble1Array.stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("a7,b6,c5,d4,e3,f2,g1", notation);

    List<Square> squareDouble2Array = BoardUtils.getSquareArray(0, 1, 8, 60, true);
    notation = squareDouble2Array.stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("b8,c7,d6,e5,f4,g3,h2", notation);
  }

  @Test
  public void get_triple_diagonals_sub() throws Exception {
    List<Square> squareDouble1Array = BoardUtils.getSquareArray(2, 0, 8, 60, false);
    String notation = squareDouble1Array.stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("h6,g5,f4,e3,d2,c1", notation);

    List<Square> squareDouble2Array = BoardUtils.getSquareArray(-2, 0, 8, 60, false);
    notation = squareDouble2Array.stream().map(ICoordinates::toNotation).collect(Collectors.joining(","));
    assertEquals("f8,e7,d6,c5,b4,a3", notation);
  }
}