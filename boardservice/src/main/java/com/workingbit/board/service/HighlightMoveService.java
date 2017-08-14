package com.workingbit.board.service;

import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.board.function.TrinaryFunction;
import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.IBoardContainer;
import com.workingbit.share.domain.IDraught;
import com.workingbit.share.domain.ISquare;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
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
public class HighlightMoveService {

  /**
   * possible directions of moving
   */
  private final List<Pair<Integer, Integer>> dirs;
  private IBoardContainer board;
  private ISquare selectedSquare;
  private HashMap<Pair<Integer, Integer>, TrinaryFunction<Integer>> diagonal;
  private EnumRules rules;

  HighlightMoveService(IBoardContainer board, Square selectedSquare, EnumRules rules) throws BoardServiceException {
    if (selectedSquare == null || selectedSquare.getDraught() == null) {
      throw new BoardServiceException("Selected square without placed draught");
    }
    selectedSquare.setPointDraught(selectedSquare.getDraught());
    selectedSquare.getDraught().setHighlighted(true);
    board.setSelectedSquare(selectedSquare);

    this.diagonal = new HashMap<>();
    this.diagonal.put(Pair.of(-1, -1), HighlightMoveService::mainDiagonal);
    this.diagonal.put(Pair.of(1, 1), HighlightMoveService::mainDiagonal);
    this.diagonal.put(Pair.of(1, -1), HighlightMoveService::subDiagonal);
    this.diagonal.put(Pair.of(-1, 1), HighlightMoveService::subDiagonal);

    this.dirs = Arrays.asList(
        Pair.of(-1, -1),
        Pair.of(1, 1),
        Pair.of(1, -1),
        Pair.of(-1, 1));
    this.board = board;
    this.rules = rules;
    this.selectedSquare = selectedSquare;
  }

  public static HighlightMoveService getService(IBoardContainer board, Square selectedSquare, EnumRules rules) throws BoardServiceException {
      return new HighlightMoveService(board, selectedSquare, rules);
  }

  public Map<String, Object> findAllowedMoves() throws BoardServiceException {
    return walk(selectedSquare, 0);
  }

  /**
   * Walk through desk
   *
   * @param selectedSquare square for which we do the algorithm
   * @param deep           how deep in recursion
   * @return {allow, beaten}
   */
  private Map<String, Object> walk(ISquare selectedSquare, int deep) throws BoardServiceException {
    List<ISquare> allowedMoves = new ArrayList<>();
    List<IDraught> beatenMoves = new ArrayList<>();
    int dimension = getBoardDimension();
    int mainDiagonal = selectedSquare.getV() - selectedSquare.getH();
    int subDiagonal = dimension - selectedSquare.getV() - selectedSquare.getH();
    List<Square> squares = board.getSquares();
    // split board's square list on rows
    Stream<List<Square>> iterateRows = getRows(dimension, squares);
    Iterator<List<Square>> rowsIterator = iterateRows.parallel().iterator();
    while (rowsIterator.hasNext()) {
      // get next row
      List<Square> next = rowsIterator.next();
      for (ISquare currentSquare : next) {
        Pair<Integer, Integer> distanceVH = getDistanceVH(selectedSquare, currentSquare);
        // if selected draught is not queen and we near of it other words not far then 2 squares then go next else skip this square
        if (!currentSquare.isMain() // if we on the main black square
            || !selectedSquare.getPointDraught().isQueen() && (abs(distanceVH.getLeft()) > 1 || abs(distanceVH.getRight()) > 1)
            || currentSquare.equals(selectedSquare)) {
          continue;
        }
        // if we on main diagonal or on sub diagonal of given square, so check square pos left and right
        if (isSquareOnMainDiagonal(mainDiagonal, currentSquare) || isSquareOnSubDiagonal(dimension, subDiagonal, currentSquare)) {
          List<Pair<Integer, Integer>> forwardDirs = getForwardDirs(selectedSquare, currentSquare);
          for (Pair<Integer, Integer> curDir : forwardDirs) {
            Optional<ISquare> nextSquare = mustBeat(selectedSquare.getPointDraught().isBlack(), curDir, currentSquare, selectedSquare);
            if (nextSquare.isPresent()) {
              Map<String, Object> walk = walk(nextSquare.get(), deep);
              allowedMoves.add(nextSquare.get());
              beatenMoves.add(nextSquare.get().getPointDraught());
            }
            // if we can move forward by dir then add to allowed moves
            else if (canMove(selectedSquare, currentSquare)) {
              allowedMoves.add(currentSquare);
            }
          }
        }
      }
    }
    List<ISquare> allowedAfterBeat = allowedMoves
        .stream()
        .filter(square -> square.getPointDraught() != null)
        .collect(Collectors.toList());
    return new HashMap<String, Object>() {{
      List<ISquare> completeAllowed = allowedAfterBeat.isEmpty() ? allowedMoves : allowedAfterBeat;
      put(allowed.name(), completeAllowed);
      put(beaten.name(), beatenMoves);
    }};
  }

  private List<Pair<Integer, Integer>> getForwardDirs(ISquare selectedSquare, ISquare currentSquare) {
    Pair<Integer, Integer> dirVH = getDirVH(selectedSquare, currentSquare);
    List<Pair<Integer, Integer>> forwardDirs = new ArrayList<>();
    if (currentSquare.isOccupied() || selectedSquare.getPointDraught().isQueen()) {
      forwardDirs.addAll(dirs.stream()
          .filter(d -> !d.equals(Pair.of(dirVH.getLeft() * -1, dirVH.getRight() * -1)))
          .collect(Collectors.toList()));
    } else {
      forwardDirs.add(dirVH);
    }
    return forwardDirs;
  }

  private static Pair<Integer, Integer> getDirVH(ISquare selectedSquare, ISquare currentSquare) {
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

  private boolean isSquareOnSubDiagonal(int dimension, int subDiagonal, ISquare square) {
    return dimension - square.getV() - square.getH() == subDiagonal;
  }

  private boolean isSquareOnMainDiagonal(int mainDiagonal, ISquare square) {
    return square.getV() - square.getH() == mainDiagonal;
  }

  private boolean canMove(ISquare selectedSquare, ISquare currentSquare) {
    boolean black = selectedSquare.getPointDraught().isBlack();
    boolean queen = selectedSquare.getPointDraught().isQueen();
    boolean beaten = selectedSquare.getPointDraught().isBeaten();
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
   *
//   * @param black
//   * @param sourceSquare
//   * @param dir
//   * @param deep
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

  private static Optional<ISquare> nextSquareByDir(IBoardContainer board, ISquare source, Pair<Integer, Integer> dir) {
    return findSquareByVH(board, source.getV() + dir.getLeft(), source.getH() + dir.getRight());
  }

  /**
   * Return allowed square for move after beat
   * @param black
   * @param dir
   * @param currentSquare
   * @param selectedSquare
   * @return
   */
  private Optional<ISquare> mustBeat(boolean black, Pair<Integer, Integer> dir, ISquare currentSquare, ISquare selectedSquare) throws BoardServiceException {
    Optional<ISquare> nextSquareOpt = nextSquareByDir(board, currentSquare, dir);
    return nextSquareOpt.map(nextSquare -> {
      IDraught currentDraught = currentSquare.getDraught();
      int dimension = getBoardDimension();
      int mainDiagonal = selectedSquare.getV() - selectedSquare.getH();
      int subDiagonal = dimension - selectedSquare.getV() - selectedSquare.getH();
      if (currentSquare.isOccupied()
          // current square is opposite color
          && currentDraught.isBlack() != black
          && (isSquareOnMainDiagonal(mainDiagonal, nextSquare)
          || isSquareOnSubDiagonal(dimension, subDiagonal, nextSquare))
          // next square empty
          && !nextSquare.isOccupied()) {
        // store new point for recursion in next square
        currentDraught.setBeaten(true);
        nextSquare.setPointDraught((Draught) currentDraught);
        return nextSquare;
      }
      return null;
    });
  }

  private static int mainDiagonal(int h, int v, int dim) {
    return h - v;
  }

  private static int subDiagonal(int h, int v, int dim) {
    return dim - h - v;
  }
}
