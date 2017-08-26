package com.workingbit.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    List<Square> squares = new ArrayList<>();
    List<Draught> whiteDraughts = new ArrayList<>();
    List<Draught> blackDraughts = new ArrayList<>();
    for (int v = 0; v < rules.getDimension(); v++) {
      for (int h = 0; h < rules.getDimension(); h++) {
        Draught draught = new Draught(v, h, rules.getDimension(), true);
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
        Square square = new Square(v, h, rules.getDimension(), (h + v + 1) % 2 == 0, squareSize, draught);
        squares.add(square);
      }
    }
    return new BoardContainer(squares, whiteDraughts, blackDraughts, null);
  }

  /**
   * Find variable link to square from board
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
