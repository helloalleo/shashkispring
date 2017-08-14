package com.workingbit.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Square;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by Aleksey Popryaduhin on 20:56 11/08/2017.
 */
class BoardUtils {

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
