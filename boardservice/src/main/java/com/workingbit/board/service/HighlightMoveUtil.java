package com.workingbit.board.service;

import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.workingbit.board.common.EnumSearch.allowed;
import static com.workingbit.board.common.EnumSearch.beaten;
import static com.workingbit.board.service.BoardUtils.isSubDiagonal;

/**
 * Created by Aleksey Popryaduhin on 19:39 10/08/2017.
 */
class HighlightMoveUtil {

  private Square selectedSquare;

  private HighlightMoveUtil(BoardContainer board, Square selectedSquare) throws BoardServiceException {
    if (selectedSquare == null || selectedSquare.getDraught() == null) {
      throw new BoardServiceException("Selected square without placed draught");
    }
    this.selectedSquare = selectedSquare;
    selectedSquare.getDraught().setHighlighted(true);
    board.setSelectedSquare(selectedSquare);
  }

  static Optional<Map<String, Object>> highlight(Board board, Square selectedSquare) throws BoardServiceException, ExecutionException, InterruptedException {
    try {
      // highlight moves for the selected square
      HighlightMoveUtil highlightMoveUtil = new HighlightMoveUtil(board.getCurrentBoard(), selectedSquare);
      return Optional.of(highlightMoveUtil.findAllMoves());
    } catch (BoardServiceException e) {
      return Optional.empty();
    }
  }

  /**
   * Entry point for initially selected square
   */
  private Map<String, Object> findAllMoves() throws BoardServiceException {
    List<Square> allowedMoves = new ArrayList<>();
    List<Square> beatenMoves = new ArrayList<>();
    Draught draught = selectedSquare.getDraught();
    boolean black = draught.isBlack();
    boolean queen = draught.isQueen();
    findBeatenMovesOnDiagonalsOfSelectedSquare(selectedSquare, black, queen, beatenMoves, allowedMoves);
    if (beatenMoves.isEmpty()) {
      findAllowedMoves(selectedSquare, allowedMoves, black, queen);
    }
    Map<String, Object> allowedAndBeatenMap = new HashMap<>();
    allowedAndBeatenMap.put(allowed.name(), allowedMoves);
    allowedAndBeatenMap.put(beaten.name(), beatenMoves);
    return allowedAndBeatenMap;
  }

  private void findAllowedMoves(Square selectedSquare, List<Square> allowedMoves, boolean down, boolean queen) {
    List<List<Square>> diagonals = selectedSquare.getDiagonals();
    for (List<Square> diagonal : diagonals) {
      int indexOfSelected = diagonal.indexOf(selectedSquare);
      if (indexOfSelected != -1) {
        if (!queen) {
          findAllowed(diagonal, selectedSquare, down, allowedMoves);
        } else {
          findAllowedForQueen(diagonal, selectedSquare, down, allowedMoves);
          findAllowedForQueen(diagonal, selectedSquare, !down, allowedMoves);
        }
      }
    }
  }

  private void findAllowedForQueen(List<Square> diagonal, Square selectedSquare, boolean down, List<Square> allowedMoves) {
    ListIterator<Square> squareListIterator = diagonal.listIterator(diagonal.indexOf(selectedSquare));
    while (down && squareListIterator.hasNext() || !down && squareListIterator.hasPrevious()) {
      findAllowedUsingIterator(down, allowedMoves, squareListIterator);
    }
  }

  private void findAllowed(List<Square> diagonal, Square selectedSquare, boolean black, List<Square> allowedMoves) {
    ListIterator<Square> squareListIterator = diagonal.listIterator(diagonal.indexOf(selectedSquare));
    findAllowedUsingIterator(black, allowedMoves, squareListIterator);
  }

  private void findAllowedUsingIterator(boolean black, List<Square> allowedMoves, ListIterator<Square> squareListIterator) {
    Square next = black ? squareListIterator.next() : squareListIterator.previous();
    if (canMove(next)) {
      allowedMoves.add(next);
    }
  }

  private void findBeatenMovesOnDiagonalsOfSelectedSquare(Square selectedSquare, boolean black, boolean queen, List<Square> beatenMoves, List<Square> allowedMoves) throws BoardServiceException {
    List<List<Square>> diagonals = selectedSquare.getDiagonals();
    int indexOfSelected;
    Set<Square> beatenMovesSet = new HashSet<>();
    List<Square> a = new ArrayList<>();
    for (List<Square> diagonal : diagonals) {
      indexOfSelected = diagonal.indexOf(selectedSquare);
      if (indexOfSelected != -1) {
        List<Square> newBeaten = new ArrayList<>();
        walkOnDiagonal(selectedSquare, black, queen, diagonal, newBeaten, a);
        beatenMovesSet.addAll(newBeaten);
      }
    }
    allowedMoves.addAll(new HashSet<>(a));
    beatenMoves.addAll(beatenMovesSet);
  }

  private void walkOnDiagonal(Square selectedSquare, boolean down, boolean queen, List<Square> diagonal, List<Square> beatenMoves, List<Square> allowedMoves) throws BoardServiceException {
    findBeatenMovesOnHalfDiagonal(diagonal, selectedSquare, down, queen, 0, true, beatenMoves, allowedMoves);
    findBeatenMovesOnHalfDiagonal(diagonal, selectedSquare, !down, queen, 0, true, beatenMoves, allowedMoves);
  }

  private void findBeatenMovesOnHalfDiagonal(List<Square> diagonal, Square selectedSquare, boolean down, boolean queen, int deep, boolean cross, List<Square> beatenMoves, List<Square> allowedMoves) throws BoardServiceException {
//    if (deep > 0) {
//      return;
//    }
    int indexOfSelected = diagonal.indexOf(selectedSquare);
    ListIterator<Square> squareListIterator = diagonal.listIterator(indexOfSelected);
    Square next, previous = selectedSquare;
    deep++;
    boolean mustBeat;
    List<Square> walkAllowedMoves = new ArrayList<>();
    do {
      if (!((down && squareListIterator.hasNext()) || (!down && squareListIterator.hasPrevious()))) {
        break;
      }
      next = down ? squareListIterator.next() : squareListIterator.previous();
      System.out.println("Next " + next.toNotation());
      mustBeat = mustBeat(next, previous);
      if (mustBeat) {
        if (beatenMoves.contains(previous)) {
          System.out.print("prev " + previous.toNotation() + ", ");
          System.out.println(beatenMoves.stream().map(Square::toNotation).collect(Collectors.toList()));
//          beatenMoves.add(previous);
//          return;
        }
        previous.getDraught().setBeaten(true);
        beatenMoves.add(previous);
        cross = true;
        System.out.println("beaten " + previous.toNotation());
        if (!queen) {
          allowedMoves.add(next);
          walkCrossDiagonalForBeaten(next, previous, down, deep, false, cross, beatenMoves, allowedMoves);
        }
      } else if (isDraughtWithSameColor(next)) {
        return;
      }
      if (!beatenMoves.isEmpty() && !beatenMoves.contains(next) && queen) {
        if (cross) {
          walkCrossDiagonalForBeaten(next, previous, down, deep, true, cross, beatenMoves, allowedMoves);
          if (!allowedMoves.contains(next) && canMove(next)) {
            allowedMoves.add(next);
          }
        } else {
          System.out.print("deep " + deep + " ");
          System.out.println("!cross " + next.toNotation() + " " + beatenMoves.stream().map(Square::toNotation).collect(Collectors.toList()));
        }
//        if (!allowedMoves.contains(next) && canMove(next)) {
//          allowedMoves.add(next);
//        } else {
//          walkAllowedMoves.add(next);
//        }
        System.out.println(next.toNotation() + " " + beatenMoves.stream().map(Square::toNotation).collect(Collectors.toList()));
      }
      previous = next;
    }
    while ((down && squareListIterator.hasNext()) || (!down && squareListIterator.hasPrevious()));
    if (beatenMoves.size() == 1) {
      allowedMoves.addAll(walkAllowedMoves);
    }
  }

  private void walkCoDiagonalForAllowed(boolean down, Square next, Square previous, List<Square> allowedMoves) {
    for (List<Square> diagonal : next.getDiagonals()) {
      if (isSubDiagonal(diagonal, Arrays.asList(previous, next))) {
        findAllowedForQueen(diagonal, next, down, allowedMoves);
      }
    }
  }

  private void walkCrossDiagonalForBeaten(Square next, Square previous, boolean down, int deep, boolean queen, boolean cross, List<Square> beatenMoves, List<Square> allowedMoves) throws BoardServiceException {
    for (List<Square> diagonal : next.getDiagonals()) {
      if (!isSubDiagonal(diagonal, Arrays.asList(previous, next))) {
        findBeatenMovesOnHalfDiagonal(diagonal, next, down, queen, deep, false, beatenMoves, allowedMoves);
        findBeatenMovesOnHalfDiagonal(diagonal, next, !down, queen, deep, false, beatenMoves, allowedMoves);
      }
    }
  }

  private boolean isDraughtWithSameColor(Square next) {
    return next.isOccupied() && next.getDraught().isBlack() == this.selectedSquare.getDraught().isBlack();
  }

  private boolean canMove(Square nextSquare) {
    return !nextSquare.isOccupied();
  }

  /**
   * Return allowed square for move after beat
   *
   * @param nextSquare square after previous
   * @return must or not beat
   */
  private boolean mustBeat(Square nextSquare, Square previousSquare) throws BoardServiceException {
    return previousSquare.isOccupied()
        && previousSquare.getDraught().isBlack() != this.selectedSquare.getDraught().isBlack()
        && !nextSquare.isOccupied();
  }
}
