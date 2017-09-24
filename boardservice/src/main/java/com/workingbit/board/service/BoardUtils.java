package com.workingbit.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.board.exception.BoardServiceError;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;
import com.workingbit.share.model.EnumRules;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.workingbit.board.common.AppConstants.INTERNAL_SERVER_ERROR;
import static com.workingbit.board.service.HighlightMoveService.highlightedAssignedMoves;

/**
 * Created by Aleksey Popryaduhin on 20:56 11/08/2017.
 */
public class BoardUtils {

  /**
   * Fill board with draughts
   *
   * @param fillBoard
   * @param black     is player plays black?
   * @param rules
   * @return
   */
  static Board initBoard(boolean fillBoard, boolean black, EnumRules rules) {
    Board boardBox = new Board(black, rules);
    return updateBoard(fillBoard, false, boardBox);
  }

  static Board updateBoard(Board board) {
    return updateBoard(false, true, board);
  }

  private static Board updateBoard(boolean fillBoard, boolean update, Board board) {
    Board boardClone = (Board) board.deepClone();
//    board.getAssignedSquares().forEach(square -> {
//      square.setHighlighted(false);
//      square.setDraught(null);
//    });
    EnumRules rules = boardClone.getRules();
    boolean black = boardClone.isBlack();

    List<Draught> blackDraughts = new ArrayList<>();
    List<Draught> whiteDraughts = new ArrayList<>();
    Map<String, Draught> blackDraughtsExisted = boardClone.getBlackDraughts();
    Map<String, Draught> whiteDraughtsExisted = boardClone.getWhiteDraughts();
    List<Square> boardSquares = getAssignedSquares(rules.getDimension());
    for (Square square : boardSquares) {
      int v = square.getV(), h = square.getH();
      if (update) {
        Draught blackDraught = blackDraughtsExisted.get(square.getNotation());
        Draught whiteDraught = whiteDraughtsExisted.get(square.getNotation());
        if (blackDraught != null) {
          square.setDraught(blackDraught);
        } else if (whiteDraught != null) {
          square.setDraught(whiteDraught);
        }
      } else if (fillBoard) {
        if (v < rules.getRowsForDraughts()) {
          placeDraught(!black, rules, blackDraughts, square, v, h);
        } else if (v >= rules.getDimension() - rules.getRowsForDraughts() && v < rules.getDimension()) {
          placeDraught(black, rules, whiteDraughts, square, v, h);
        }
      }
    }
    boardClone.setAssignedSquares(boardSquares);
    updateMoveSquaresDimensionAndDiagonals(boardClone);

    List<Square> squares = getSquares(boardSquares, rules.getDimension());
    boardClone.setSquares(squares);
    return boardClone;
  }

  private static void placeDraught(boolean black, EnumRules rules, List<Draught> draughts, Square square, int v, int h) {
    Draught draught = new Draught(v, h, rules.getDimension());
    draught.setBlack(black);
    draughts.add(draught);
    square.setDraught(draught);
  }

  static List<Square> getSquareArray(int offset, int dim, boolean main) {
    List<Square> squares = new ArrayList<>();
    for (int v = 0; v < dim; v++) {
      for (int h = 0; h < dim; h++) {
        if (((v + h + 1) % 2 == 0)
            && (main && (v - h + offset) == 0
            || !main && (v + h - offset) == dim - 1)) {
          Square square = new Square(v, h, dim, main);
          squares.add(square);
        }
      }
    }
    return squares;
  }

  static List<List<Square>> getDiagonals(int dim, boolean main) {
    List<List<Square>> diagonals = new ArrayList<>(dim - 2);
    for (int i = -dim; i < dim - 1; i++) {
      if ((i == 1 - dim) && main) {
        continue;
      }
      List<Square> diagonal = BoardUtils.getSquareArray(i, dim, main);
      if (!diagonal.isEmpty()) {
        diagonals.add(diagonal);
      }
    }
    return diagonals;
  }

  public static boolean isSubDiagonal(List<Square> diagonal, List<Square> subDiagonal) {
    return subDiagonal.stream().allMatch(diagonal::contains);
  }

  /**
   * Assign square subdiagonal and main diagonal. Assign diagonal's squares link to squares
   *
   * @param dim
   * @return
   */
  private static List<Square> getAssignedSquares(int dim) {
    List<List<Square>> mainDiagonals = getDiagonals(dim, true);
    List<List<Square>> subDiagonals = getDiagonals(dim, false);

    List<Square> squares = new ArrayList<>();
    for (List<Square> subDiagonal : subDiagonals) {
      for (Square subSquare : subDiagonal) {
        subSquare.addDiagonal(subDiagonal);
        squares.add(subSquare);
        subDiagonal.set(subDiagonal.indexOf(subSquare), subSquare);
        for (List<Square> mainDiagonal : mainDiagonals) {
          for (Square mainSquare : mainDiagonal) {
            if (subSquare.equals(mainSquare)) {
              subSquare.addDiagonal(mainDiagonal);
              mainDiagonal.set(mainDiagonal.indexOf(subSquare), subSquare);
            }
          }
        }
      }
    }

    return squares;
  }

  private static List<Square> getSquares(List<Square> diagonals, int dim) {
    List<Square> squares = new ArrayList<>();
    List<Square> collect = diagonals
        .stream()
        .sorted(Comparator.comparingInt(Square::getV))
        .collect(Collectors.toList());
    Iterator<Square> iterator = collect.iterator();
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        if ((i + j) % 2 == 0) {
          squares.add(null);
        } else if (iterator.hasNext()) {
          squares.add(iterator.next());
        }
      }
    }
    return squares;
  }

//  private static boolean mainDiagonal(int v, int h, int dim) {
//    return h - v;
//  }
//
//  private static boolean isSubDiagonal(int v, int h, int dim) {
//    return dim - h - v;
//  }

  /**
   * Find variable link to square from board
   *
   * @param board
   * @param square
   * @return
   */
  static Optional<Square> findSquareLink(Board board, Square square) {
    if (square == null) {
      return Optional.empty();
    }
    return findSquareByVH(board, square.getV(), square.getH());
  }

  static Optional<Square> findSquareByVH(Board board, int v, int h) {
    for (Square square : board.getAssignedSquares()) {
      if (square.getH() == h && square.getV() == v) {
        return Optional.of(square);
      }
    }
    return Optional.empty();
  }

  static Optional<Square> findSquareByNotation(Board board, String notation) {
    if (StringUtils.isBlank(notation)) {
      return Optional.empty();
    }
    for (Square square : board.getAssignedSquares()) {
      if (square.getNotation().equals(notation)) {
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

  static Supplier<BoardServiceError> getBoardServiceExceptionSupplier(String message) {
    return () -> new BoardServiceError(message);
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

  public static void addDraught(Board board, String notation, Draught draught) throws BoardServiceError {
    if (draught == null) {
      return;
    }
    addDraught(board, notation, draught.isBlack(), draught.isQueen(), draught.isBeaten());
  }

  public static void addDraught(Board board, String notation, boolean black) {
    addDraught(board, notation, black, false, false);
  }

  public static void addDraught(Board board, String notation, boolean black, boolean queen) {
    addDraught(board, notation, black, queen, false);
  }

  public static void addDraught(Board board, String notation, boolean black, boolean queen, boolean remove) {
    Optional<Square> squareOptional = findSquareByNotation(board, notation);
    squareOptional.ifPresent(square -> {
      Draught draught = null;
      if (!remove) {
        draught = new Draught(square.getV(), square.getH(), square.getDim(), black, queen);
        if (black && !isOverloadDraughts(board, true)) {
          board.addBlackDraughts(notation, draught);
        } else if (!isOverloadDraughts(board, black)) {
          board.addWhiteDraughts(notation, draught);
        }
      } else {
        if (black) {
          board.getBlackDraughts().remove(notation);
        } else {
          board.getWhiteDraughts().remove(notation);
        }
      }
      square.setDraught(draught);
    });
  }

  private static boolean isOverloadDraughts(Board board, boolean black) {
    return black ? board.getBlackDraughts().size() >= board.getRules().getDraughtsCount()
        : board.getWhiteDraughts().size() >= board.getRules().getDraughtsCount();
  }

  public static void removeDraught(Board board, String notation, boolean black) {
    addDraught(board, notation, black, false, true);
  }

  public static void moveDraught(Square selectedSquare, Square nextSquare, Board board) {
    highlightedBoard(board, selectedSquare);
    board.setNextSquare(nextSquare);
    moveDraught(board);
    highlightedBoard(board, nextSquare);
  }

  public static void highlightedBoard(Board board, Square selectedSquare) {
    resetBoardHighlight(board);
    highlightedAssignedMoves(selectedSquare);
    List<Square> squares = getSquares(board.getAssignedSquares(), board.getRules().getDimension());
    board.setSquares(squares);
  }

  private static void resetBoardHighlight(Board board) {
    board.getAssignedSquares().forEach(square -> {
      square.setHighlighted(false);
    });
  }

  private static void moveDraught(Board board) {
    Square sourceSquare = board.getSelectedSquare();
    Square targetSquare = board.getNextSquare();
    if (!targetSquare.isHighlighted()
        || sourceSquare == null
        || !sourceSquare.isOccupied()
        || sourceSquare.getDraught().isBlack() != board.isBlack()) {
      throw new BoardServiceError("Unable to move the draught");
    }
    Draught draught = sourceSquare.getDraught();
    BoardUtils.addDraught(board, targetSquare.getNotation(), draught);
    BoardUtils.removeDraught(board, sourceSquare.getNotation(), draught.isBlack());
    targetSquare = BoardUtils.findSquareByNotation(board, targetSquare.getNotation())
        .orElseThrow(getBoardServiceExceptionSupplier(INTERNAL_SERVER_ERROR));
    board.setNextSquare(null);
    board.setPreviousSquare(board.getSelectedSquare());
    board.getPreviousSquare().setDraught(null);
    board.setSelectedSquare(targetSquare);
    targetSquare.setHighlighted(true);
  }

  public static void updateMoveSquaresHighlight(Board currentBoard, Board origBoard) {
    Square selectedSquare = findSquareLink(currentBoard, origBoard.getSelectedSquare()).orElse(null);
    if (selectedSquare != null) {
      currentBoard.setSelectedSquare(updateSquare(selectedSquare, origBoard.getSelectedSquare()));
    }
    Square nextSquare = findSquareLink(currentBoard, origBoard.getNextSquare()).orElse(null);
    if (nextSquare != null) {
      currentBoard.setNextSquare(updateSquare(nextSquare, origBoard.getNextSquare()));
    }
    Square previousSquare = findSquareLink(currentBoard, origBoard.getPreviousSquare()).orElse(null);
    if (previousSquare != null) {
      currentBoard.setPreviousSquare(updateSquare(previousSquare, origBoard.getPreviousSquare()));
    }
    updateMoveDraughtsNotation(selectedSquare);
    updateMoveDraughtsNotation(nextSquare);
    updateMoveDraughtsNotation(previousSquare);
  }

  private static Square updateSquare(Square selectedSquare, Square origSquare) {
    return selectedSquare.highlight(origSquare.isHighlighted());
  }

  private static void updateMoveDraughtsNotation(Square square) {
    if (square != null && square.isOccupied()) {
      square.getDraught().setDim(square.getDim());
    }
  }

  public static void updateMoveSquaresDimensionAndDiagonals(Board currentBoard) {
    currentBoard.setSelectedSquare(updateSquareDimension(currentBoard.getSelectedSquare(), currentBoard));
    currentBoard.setNextSquare(updateSquareDimension(currentBoard.getNextSquare(), currentBoard));
    currentBoard.setPreviousSquare(updateSquareDimension(currentBoard.getPreviousSquare(), currentBoard));
  }

  private static Square updateSquareDimension(Square square, Board board) {
    if (square != null) {
      Square updated = findSquareLink(board, square)
          .orElseThrow(getBoardServiceExceptionSupplier("Unable to find square"));
//      updated.setDraught(square.getDraught());
//      updateMoveDraughtsNotation(updated);
      return updated;
    }
    return null;
  }
}
