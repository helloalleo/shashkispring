package com.workingbit.board.service;

import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.board.function.TrinaryFunction;
import com.workingbit.board.model.MoveTracer;
import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.workingbit.board.common.EnumSearch.allowed;
import static com.workingbit.board.common.EnumSearch.beaten;
import static com.workingbit.board.service.BoardUtils.findSquareByVH;
import static com.workingbit.board.service.BoardUtils.getDistanceVH;
import static java.lang.Math.abs;

/**
 * Created by Aleksey Popryaduhin on 19:39 10/08/2017.
 */
public class HighlightMoveUtil {

  /**
   * possible directions of moving
   */
  private final List<Pair<Integer, Integer>> dirs;
  private BoardContainer board;
  private Square selectedSquare;
  private HashMap<Pair<Integer, Integer>, TrinaryFunction<Integer>> diagonal;
  private EnumRules rules;

  HighlightMoveUtil(BoardContainer board, Square selectedSquare, EnumRules rules) throws BoardServiceException {
    if (selectedSquare == null || selectedSquare.getDraught() == null) {
      throw new BoardServiceException("Selected square without placed draught");
    }
//    selectedSquare.setBreakpointSquare(null);
    selectedSquare.getDraught().setHighlighted(true);
    board.setSelectedSquare(selectedSquare);

    this.diagonal = new HashMap<>();
    this.diagonal.put(Pair.of(-1, -1), HighlightMoveUtil::mainDiagonal);
    this.diagonal.put(Pair.of(1, 1), HighlightMoveUtil::mainDiagonal);
    this.diagonal.put(Pair.of(1, -1), HighlightMoveUtil::subDiagonal);
    this.diagonal.put(Pair.of(-1, 1), HighlightMoveUtil::subDiagonal);

    this.dirs = Arrays.asList(
        Pair.of(-1, -1),
        Pair.of(1, 1),
        Pair.of(1, -1),
        Pair.of(-1, 1));
    this.board = board;
    this.rules = rules;
    this.selectedSquare = selectedSquare;
  }

  private CompletableFuture<List<Square>> filterNotOnMainAndSelectedSquares(List<Square> squares, Square selectedSquare) {
    // get squares without selected
    squares.remove(selectedSquare);
    // first filter
    return CompletableFuture.completedFuture(squares)
        .thenApply(squareStream -> filterNotOnMainAndSelectedSquaresFunction(squareStream, selectedSquare));
  }

  /**
   * Initial filtering. Returns On Main part of board and the diagonals of selected draught
   *
   * @param selectedSquare
   * @return
   */
  private CompletableFuture<List<Square>> filterNotOnMainAndSelectedSquares(Square selectedSquare) {
    return filterNotOnMainAndSelectedSquares(new ArrayList<>(board.getSquaresSet()), selectedSquare);
  }

  private CompletableFuture<List<Square>> filterQueenSquares(MoveTracer tracer) {
    // filter simple draughts
    return filterNotOnMainAndSelectedSquares(selectedSquare)
        .thenApply(squareStream -> filterQueenSquaresFunction(squareStream, selectedSquare));
  }

  /**
   * Entry point for initially selected square
   *
   * @return
   */
  public Map<String, Object> findAllMoves() throws BoardServiceException {
    List<Square> allowedMoves = new ArrayList<>();
    List<Square> beatenMoves = new ArrayList<>();
    Draught draught = selectedSquare.getDraught();
    boolean black = draught.isBlack();
    Set<List<Square>> diagonals = findBeatenMoves(selectedSquare, allowedMoves, beatenMoves, black);
    if (beatenMoves.isEmpty()) {
      for (List<Square> squares : diagonals) {
        int indexOfSelected = squares.indexOf(selectedSquare);
        if (indexOfSelected != -1) {
          ListIterator<Square> squareListIterator = squares.listIterator(indexOfSelected);
          if (black) {
            allowedMoves.add(squareListIterator.next());
          } else {
            Square next = squareListIterator.previous();
            if (canMove(selectedSquare, next)) {
              allowedMoves.add(next);
            }
          }
        }
      }
    }
    Map<String, Object> allowedAndBeatenMap = new HashMap<>();
    allowedAndBeatenMap.put(allowed.name(), allowedMoves);
    allowedAndBeatenMap.put(beaten.name(), beatenMoves);
    return allowedAndBeatenMap;
  }

  private Set<List<Square>> findBeatenMoves(Square selectedSquare, List<Square> allowedMoves, List<Square> beatenMoves, boolean black) throws BoardServiceException {
    Set<List<Square>> diagonals = selectedSquare.getDiagonals();
    for (List<Square> squares : diagonals) {
      int indexOfSelected = squares.indexOf(selectedSquare);
      if (indexOfSelected != -1) {
        ListIterator<Square> squareListIterator = squares.listIterator(indexOfSelected);
        findBeaten(allowedMoves, beatenMoves, squareListIterator, black, selectedSquare);
      }
    }
    return diagonals;
  }

  private void findBeaten(List<Square> allowedMoves, List<Square> beatenMoves, ListIterator<Square> squareListIterator, boolean black, Square selectedSquare) throws BoardServiceException {
    Square next = black ? squareListIterator.next() : squareListIterator.previous();
    Square nextNext = black ? squareListIterator.next() : squareListIterator.previous();
    boolean mustBeat = mustBeat(next, selectedSquare, nextNext);
    if (mustBeat) {
      beatenMoves.add(next);
      allowedMoves.add(nextNext);
      findBeatenMoves(nextNext, allowedMoves, beatenMoves, black);
    }
  }

//  private List<Square> findAllowedMovesFunction(List<Square> squareStream, Square selectedSquare) {
//    Stream<Stream<?>>> supplier = findAllowedAndBeatenMovesFunction(squareStream, selectedSquare);

//    if (beaten) {
//      return supplier.get()
//          .flatMap(stream -> stream)
//          .map(pair -> ((Pair<Square, Square>) pair).getRight())
//          .filter(Objects::nonNull)
//          .collect(Collectors.toList());
//    } else {
//      return supplier.get()
//          .flatMap(stream -> stream)
//          .map(pair -> ((Pair<Square, Square>) pair).getLeft())
//          .filter(Objects::nonNull)
//          .collect(Collectors.toList());
//    }
//  }

  /**
   * Walk through desk
   *
   * @param selectedSquare square for which we do the algorithm
   * @param deep           how deep in recursion
   * @return {allow, beaten}
   */
//  private Map<String, Object> walk(Square selectedSquare, int deep) throws BoardServiceException {
//    List<Square> allowedMoves = new ArrayList<>();
//    List<Draught> beatenMoves = new ArrayList<>();
//    int dimension = getBoardDimension();
//    int mainDiagonal = selectedSquare.getV() - selectedSquare.getH();
//    int subDiagonal = dimension - selectedSquare.getV() - selectedSquare.getH();
//    Triple<List<Square>, List<Square>, List<Square>> squares = Triple.of(board.getSquares(), new ArrayList<>(), new ArrayList<>());
  // filter simple draughts
//    CompletableFuture<Stream<Square>> simpleDraughts =
//        onMain.thenApply(squareStream -> filterDraughts(squareStream, selectedSquare));

//    onMain.thenApply(squareStream -> filterQueens(squareStream, selectedSquare));
//    List<Pair<Integer, Integer>> forwardDirs = getForwardDirs(selectedSquare, );

  // split board's square list on rows
//    Stream<List<Square>> iterateRows = getRows(dimension, squares);
//    Iterator<List<Square>> rowsIterator = iterateRows.parallel().iterator();
//    while (rowsIterator.hasNext()) {
  // get next row
//      List<Square> next = rowsIterator.next();
/*
    for (Square currentSquare : squares) {
      Pair<Integer, Integer> distanceVH = getDistanceVH(selectedSquare, currentSquare);
      // if selected draught is not queen and we near of it other words not far then 2 squares then go next else skip this square
      if (!currentSquare.isMain() // if we on the main black square
          || !selectedSquare.getBreakpointSquare().isQueen() && (abs(distanceVH.getLeft()) > 1 || abs(distanceVH.getRight()) > 1)
          || currentSquare.equals(selectedSquare)) {
        continue;
      }
      // if we on main diagonal or on sub diagonal of given square, so check square pos left and right
      if (isSquareOnMainDiagonal(mainDiagonal, currentSquare) || isSquareOnSubDiagonal(dimension, subDiagonal, currentSquare)) {
        List<Pair<Integer, Integer>> forwardDirs = getForwardDirs(selectedSquare, currentSquare);
        for (Pair<Integer, Integer> curDir : forwardDirs) {
          Optional<Square> nextSquare = mustBeat(selectedSquare.getBreakpointSquare().isBlack(), curDir, currentSquare, selectedSquare);
          if (nextSquare.isPresent()) {
            Map<String, Object> walk = walk(nextSquare.get(), deep);
            allowedMoves.add(nextSquare.get());
            beatenMoves.add(nextSquare.get().getBreakpointSquare());
          }
          // if we can move forward by dir then add to allowed moves
          else if (canMove(selectedSquare, currentSquare)) {
            allowedMoves.add(currentSquare);
          }
        }
      }
//      }
    }
    List<Square> allowedAfterBeat = allowedMoves
        .stream()
        .filter(square -> square.getBreakpointSquare() != null)
        .collect(Collectors.toList());
    return new HashMap<String, Object>() {{
      List<Square> completeAllowed = allowedAfterBeat.isEmpty() ? allowedMoves : allowedAfterBeat;
      put(allowed.name(), completeAllowed);
      put(beaten.name(), beatenMoves);
    }};
    */
//    return null;
//  }

  /**
   * Filters usual draughts
   *
   * @param squareStream
   * @param selectedSquare
   * @return
   */
  private List<Square> filterQueenSquaresFunction(List<Square> squareStream, Square selectedSquare) {
    return squareStream
        .stream()
        .filter(square -> {
          Pair<Integer, Integer> distanceVH = getDistanceVH(selectedSquare, square);
          return selectedSquare.getDraught().isQueen() || abs(distanceVH.getLeft()) < 3 || abs(distanceVH.getRight()) < 3;
        })
        .collect(Collectors.toList());
  }

  /**
   * Filter main square and on the diagonals of current square
   *
   * @param squareStream
   * @param selectedSquare
   * @return
   */
  private List<Square> filterNotOnMainAndSelectedSquaresFunction(List<Square> squareStream, Square selectedSquare) {
    return squareStream
        .stream()
        .filter(Square::isMain)
        .filter(square -> isOnCross(selectedSquare, square))
        .collect(Collectors.toList());
  }

//  private Triple<List<Square>, List<Square>, List<Square>> filterNotOnMainAndSelectedSquares(Triple<List<Square>, List<Square>, List<Square>> squares) {
//    return null;
//  }

  private List<Pair<Integer, Integer>> getForwardDirs(Square selectedSquare, Square currentSquare) {
    Pair<Integer, Integer> dirVH = getDirVH(selectedSquare, currentSquare);
    List<Pair<Integer, Integer>> forwardDirs = new ArrayList<>();
    if (currentSquare.isOccupied() || selectedSquare.getDraught().isQueen()) {
      forwardDirs.addAll(dirs.stream()
          .filter(d -> !d.equals(Pair.of(dirVH.getLeft() * -1, dirVH.getRight() * -1)))
          .collect(Collectors.toList()));
    } else {
      forwardDirs.add(dirVH);
    }
    return forwardDirs;
  }

  private static Pair<Integer, Integer> getDirVH(Square selectedSquare, Square currentSquare) {
    Pair<Integer, Integer> distanceVH = getDistanceVH(selectedSquare, currentSquare);
    return Pair.of(distanceVH.getLeft() / abs(distanceVH.getLeft()),
        distanceVH.getRight() / abs(distanceVH.getRight()));
  }

  /**
   * Split board squares on chunks which means rows
   *
   * @param dimension length of chunks
   * @param squares   array to split
   * @return stream of list chunks
   */
  private Stream<List<Square>> getRows(int dimension, List<Square> squares) {
    return Stream.iterate(squares, l -> l.subList(0, dimension)).limit(squares.size() / dimension);
  }

  private int getBoardDimension() {
    return abs(rules.getDimension());
  }

  private boolean isSquareOnSubDiagonal(Square selectedSquare, Square square) {
    int dimension = getBoardDimension();
    int subDiagonal = dimension - selectedSquare.getV() - selectedSquare.getH();
    return dimension - square.getV() - square.getH() == subDiagonal;
  }

  private boolean isSquareOnMainDiagonal(Square selectedSquare, Square square) {
    int mainDiagonal = selectedSquare.getV() - selectedSquare.getH();
    return square.getV() - square.getH() == mainDiagonal;
  }

  private boolean canMove(Square selectedSquare, Square currentSquare) {
    boolean black = selectedSquare.getDraught().isBlack();
    boolean queen = selectedSquare.getDraught().isQueen();
    boolean beaten = selectedSquare.getDraught().isBeaten();
    Pair<Integer, Integer> dist = getDistanceVH(selectedSquare, currentSquare);
    return (
        // rules for draught
        !queen &&
            // we have not beat yet
            !beaten &&
            // if current square is not occupied
            !currentSquare.isOccupied() &&
            (
                // rule for white draughts, they can go only up
                !black && (dist.getLeft() == -1 && dist.getRight() == -1 || dist.getLeft() == -1 && dist.getRight() == 1)
                    || // rule for black draughts, they can go only down
                    black && (dist.getLeft() == 1 && dist.getRight() == -1 || dist.getLeft() == 1 && dist.getRight() == 1)))
//        (
        // rules for queen
//        selectedSquare.isOccupied()
//            && selectedSquare.getDraught().isQueen()
//            && selectedSquare != currentSquare
//            || !selectedSquare.getDraught().isQueen()
//            && (dir.getLeft() == -1 && dir.getRight() == -1 || dir.getLeft() == 1 && dir.getRight() == -1))
        ;
  }

  /**
   * метод возвращает поля на которые нужно стать, чтобы побить шашку
   * <p>
   * //   * @param black
   * //   * @param sourceSquare
   * //   * @param dir
   * //   * @param deep
   *
   * @returns {any}
   */
//  private List<Square> walkFront(boolean black, Square sourceSquare, Pair<Integer, Integer> dir, int deep) {
//    if (sourceSquare == null || deep >= 1) {
//      return new ArrayList<>();
//    }
//    deep++;
//    Map<Pair<Integer, Integer>, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> mustache = new HashMap<>();
//    mustache.put(Pair.of(-1, -1), Pair.of(Pair.of(-1, 1), Pair.of(1, -1)));
//    mustache.put(Pair.of(1, 1), Pair.of(Pair.of(-1, 1), Pair.of(1, -1)));
//    mustache.put(Pair.of(1, -1), Pair.of(Pair.of(1, 1), Pair.of(-1, -1)));
//    mustache.put(Pair.of(-1, 1), Pair.of(Pair.of(1, 1), Pair.of(-1, -1)));
//    int h = sourceSquare.getH();
//    int v = sourceSquare.getV();
//    List<Square> allow = new ArrayList<>(), beat = new ArrayList<>();
//    List<Square> items;
//    for (Square currentSquare : this.board.getSquares()) {
//      // go left
//      Pair<Integer, Integer> left = mustache.get(dir).getLeft();
//      Map<String, Object> walkLeft = this.walk(currentSquare, deep);
//      allow.addAll((Collection<? extends Square>) walkLeft.get(allowed.name()));
//      items = (List<Square>) walkLeft.get(beaten.name());
//      Map<String, Object> walkRight = this.walk(currentSquare, deep);
//      allow.addAll((Collection<? extends Square>) walkRight.get(allowed.name()));
//      items.addAll((Collection<? extends Square>) walkRight.get(beaten.name()));
//      if (items.size() > 0) {
//        Stream<Square> some = items.stream().filter(Objects::isNull);
//        if (some != null) {
//          int sourceDiff = diagonal.get(dir).apply(h, v, this.getBoardDimension());
//          allow = allow
//              .stream()
//              .filter((s) -> diagonal.get(dir).apply(s.getH(), s.getV(), this.getBoardDimension()) == sourceDiff)
//              .collect(Collectors.toList());
//        } else {
//          beat.add(currentSquare);
//        }
//      }
//      v += dir.getRight();
//      h += dir.getLeft();
//    }
//    int sourceDiff = this.diagonal.get(dir).apply(sourceSquare.getH(), sourceSquare.getV(), this.getBoardDimension());
//    if (beat.size() == 0) {
//      return allow.stream().filter((s) -> this.diagonal.get(dir).apply(s.getH(), s.getV(), this.getBoardDimension()) == sourceDiff).collect(Collectors.toList());
//    }
//    return beat;
//  }
  private static Optional<Square> nextSquareByDir(BoardContainer board, Square source, Pair<Integer, Integer> dir) {
    return findSquareByVH(board, source.getV() + dir.getLeft(), source.getH() + dir.getRight());
  }

  /**
   * Return allowed square for move after beat
   *
   * @param currentSquare
   * @param selectedSquare
   * @return
   */
  private boolean mustBeat(Square currentSquare, Square selectedSquare, Square nextSquare) throws BoardServiceException {
    Pair<Integer, Integer> newDir = getDirVH(selectedSquare, currentSquare);
    Draught currentDraught = currentSquare.getDraught();
    if (currentSquare.isOccupied()
        // current square is opposite color
        && currentDraught.isBlack() != this.selectedSquare.getDraught().isBlack()
        && isOnCross(selectedSquare, nextSquare)
        // next square empty
        && !nextSquare.isOccupied()) {
      // store new point for recursion in next square
      currentDraught.setBeaten(true);
      return true;
    }
    return false;
  }

//  private Square getBreakpointSquare(Square selectedSquare) {
//    return selectedSquare.getBreakpointSquare() != null ? selectedSquare.getBreakpointSquare() : this.selectedSquare;
//  }

  private static int mainDiagonal(int h, int v, int dim) {
    return h - v;
  }

  private static int subDiagonal(int h, int v, int dim) {
    return dim - h - v;
  }

  private boolean isOnCross(Square selectedSquare, Square square) {
    return (isSquareOnMainDiagonal(selectedSquare, square)
        || isSquareOnSubDiagonal(selectedSquare, square))
        && !selectedSquare.equals(square);
  }

  public static Optional<Map<String, Object>> highlight(Board board, Square selectedSquare) throws BoardServiceException, ExecutionException, InterruptedException {
    try {
      // highlight moves for the selected square
      HighlightMoveUtil highlightMoveUtil = new HighlightMoveUtil(board.getCurrentBoard(), selectedSquare, board.getRules());
      return Optional.of(highlightMoveUtil.findAllMoves());
    } catch (BoardServiceException e) {
      return Optional.empty();
    }
  }
}
