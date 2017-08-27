package com.workingbit.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.EnumDiagonals;
import com.workingbit.share.domain.impl.Square;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Supplier;

/**
 * Created by Aleksey Popryaduhin on 20:56 11/08/2017.
 */
public class BoardUtils {

  /**
   * Fill board with draughts
   *
   * @param fillBoard
   * @param black      is player plays black?
   * @param rules
   * @param squareSize size of one square
   * @return
   */
  static BoardContainer initBoard(boolean fillBoard, boolean black, EnumRules rules, Integer squareSize) {
    BoardContainer boardContainer = new BoardContainer();
    List<Draught> whiteDraughts = new ArrayList<>();
    List<Draught> blackDraughts = new ArrayList<>();
    for (int v = 0; v < rules.getDimension(); v++) {
      for (int h = 0; h < rules.getDimension(); h++) {
        Square square = new Square(v, h, rules.getDimension(), (h + v + 1) % 2 == 0, squareSize, null);
        Draught draught = new Draught(v, h, rules.getDimension(), true, square);
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
        }
      }
    }
    Map<EnumDiagonals, Square[]> diagonalsMap = boardContainer.getDiagonalsMap();

    diagonalsMap.put(EnumDiagonals.mainRoad, getSquareArray(1, 1, true, rules, squareSize));

    diagonalsMap.put(EnumDiagonals.doubleA7B8Sm, getSquareArray(1, 7, true, rules, squareSize));
    diagonalsMap.put(EnumDiagonals.doubleG1H2Sm, getSquareArray(7, 1, true, rules, squareSize));
    diagonalsMap.put(EnumDiagonals.fourthA5D8Sm, getSquareArray(1, 5, true, rules, squareSize));
    diagonalsMap.put(EnumDiagonals.fourthE1H4Sm, getSquareArray(5, 1, true, rules, squareSize));
    diagonalsMap.put(EnumDiagonals.tripleA3F8Bg, getSquareArray(1, 3, true, rules, squareSize));
    diagonalsMap.put(EnumDiagonals.tripleC1H6Bg, getSquareArray(3, 1, true, rules, squareSize));

    diagonalsMap.put(EnumDiagonals.doubleH2B8Bg, getSquareArray(8, 2, false, rules, squareSize));
    diagonalsMap.put(EnumDiagonals.doubleG1A7Bg, getSquareArray(7, 1, false, rules, squareSize));
    diagonalsMap.put(EnumDiagonals.fourthH4D8Bg, getSquareArray(8, 4, false, rules, squareSize));
    diagonalsMap.put(EnumDiagonals.fourthE1H4Sm, getSquareArray(5, 1, false, rules, squareSize));
    diagonalsMap.put(EnumDiagonals.tripleC1H6Bg, getSquareArray(3, 1, false, rules, squareSize));
    diagonalsMap.put(EnumDiagonals.tripleH6F8Sm, getSquareArray(8, 6, false, rules, squareSize));

    boardContainer.setBlackDraughts(blackDraughts);
    boardContainer.setWhiteDraughts(whiteDraughts);
    return boardContainer;
  }

  static List<Square> getSquareArray(int offsetV, int offsetH, int dim, int squareSize, boolean prime) {
    List<Square> squares = new ArrayList<>();
    for (int v = 0; v < dim; v++) {
      for (int h = 0; h < dim; h++) {
        System.out.println((v + h - offsetV - offsetH) + ", v " + v + ", h " + h + ", dim " + dim);
        if (((v + h + 1) % 2 == 0)
            && (prime && (v - offsetV) == (h - offsetH)
            || !prime && (v + h - offsetV) == dim - 1)) {
          Square square = new Square(v, h, dim, true, squareSize, null);
          squares.add(square);
        }
      }
    }
    return squares;
  }

  static Square[] getSquareArray(int offsetV, int offsetH, boolean main, EnumRules rules, int squareSize) {
    int dim = rules.getDimension();
    for (int v = 0; v < dim; v++) {
      for (int h = 0; h < dim; h++) {
//        if (((h + v + 1) % 2 == 0) && (h - v == offsetH || !main && subDiagonal(v, h, dim))) {
//          Square square = new Square(v, h, dim, true, squareSize, null);
//        }
      }
    }
    return new Square[8];
  }

//  private static boolean mainDiagonal(int v, int h, int dim) {
//    return h - v;
//  }
//
//  private static boolean subDiagonal(int v, int h, int dim) {
//    return dim - h - v;
//  }

  /**
   * Find variable link to square from board
   *
   * @param board
   * @param square
   * @return
   */
  static Optional<Square> findSquareLink(BoardContainer board, Square square) {
    return findSquareByVH(board, square.getV(), square.getH());
  }

  static Optional<Square> findSquareByVH(BoardContainer board, int v, int h) {
    for (Square square : board.getSquares()) {
      if (square.getH() == h && square.getV() == v) {
        return Optional.of(square);
      }
    }
    return Optional.empty();
  }

  static Optional<Square> findSquareByNotation(BoardContainer board, String notation) {
    if (StringUtils.isBlank(notation)) {
      return Optional.empty();
    }
    for (Square square : board.getSquares()) {
      if (square.toNotation().equals(notation)) {
        return Optional.of(square);
      }
    }
    return Optional.empty();
  }

  /**
   * Get diff between source and target if h == -1 then we go left if h == 1 go right if v == -1 go up if v == 1 go up
   *
   * @param source
   * @param target
   * @return
   */
  static Pair<Integer, Integer> getDistanceVH(Square source, Square target) {
    int vDist = target.getV() - source.getV();
    int hDist = target.getH() - source.getH();
    return Pair.of(vDist, hDist);
  }

  static Supplier<BoardServiceException> getBoardServiceExceptionSupplier(String message) {
    return () -> new BoardServiceException(message);
  }

  static <T, I> List<I> mapList(List<I> squares, ObjectMapper objectMapper, Class<T> clazz, Class<I> iclazz) {
    if (squares == null || squares.isEmpty()) {
      return Collections.emptyList();
    }
    List<I> newSquares = new ArrayList<>(squares.size());
    // leave as is. Find by Id returns HashMap of getSquares() convert so we need to convert it to Square
    for (int i = 0; i < squares.size(); i++) {
      I square = iclazz.cast(objectMapper.convertValue(squares.get(i), clazz));
      newSquares.add(square);
    }
    return newSquares;
  }
}
