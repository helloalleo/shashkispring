package com.workingbit.board.service;

import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;

import java.util.*;
import java.util.concurrent.ExecutionException;

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
    findBeatenMovesOnDiagonalsOfSelectedSquare(beatenMoves, allowedMoves, selectedSquare, black, queen);
    if (beatenMoves.isEmpty()) {
      findAllowedMoves(selectedSquare, allowedMoves, black, queen);
    }
    Map<String, Object> allowedAndBeatenMap = new HashMap<>();
    allowedAndBeatenMap.put(allowed.name(), allowedMoves);
    allowedAndBeatenMap.put(beaten.name(), beatenMoves);
    return allowedAndBeatenMap;
  }

  private void findAllowedMoves(Square selectedSquare, List<Square> allowedMoves, boolean black, boolean queen) {
    List<List<Square>> diagonals = selectedSquare.getDiagonals();
    for (List<Square> diagonal : diagonals) {
      int indexOfSelected = diagonal.indexOf(selectedSquare);
      if (indexOfSelected != -1) {
        ListIterator<Square> squareListIterator = diagonal.listIterator(indexOfSelected);
        if (!queen) {
          findAllowed(allowedMoves, black, squareListIterator);
        } else {
          findAllowedForQueen(allowedMoves, black, squareListIterator);
          squareListIterator = diagonal.listIterator(indexOfSelected);
          findAllowedForQueen(allowedMoves, !black, squareListIterator);
        }
      }
    }
  }

  private void findAllowedForQueen(List<Square> allowedMoves, boolean black, ListIterator<Square> squareListIterator) {
    do {
      findAllowed(allowedMoves, black, squareListIterator);
    } while (black && squareListIterator.hasNext() || !black && squareListIterator.hasPrevious());
  }

  private void findAllowed(List<Square> allowedMoves, boolean black, ListIterator<Square> squareListIterator) {
    Square next = black ? squareListIterator.next() : squareListIterator.previous();
    if (canMove(next)) {
      allowedMoves.add(next);
    }
  }

  private void findBeatenMovesOnDiagonalsOfSelectedSquare(List<Square> beatenMoves, List<Square> allowedMoves, Square selectedSquare, boolean black, boolean queen) throws BoardServiceException {
    List<List<Square>> diagonals = selectedSquare.getDiagonals();
    int indexOfSelected;
    for (List<Square> diagonal : diagonals) {
      indexOfSelected = diagonal.indexOf(selectedSquare);
      if (indexOfSelected != -1) {
        List<Square> newBeaten = new ArrayList<>();
        walkOnDiagonal(selectedSquare, newBeaten, black, queen, diagonal, allowedMoves);
        beatenMoves.addAll(newBeaten);
      }
    }
  }

  private void walkOnDiagonal(Square selectedSquare, List<Square> beatenMoves, boolean down, boolean queen, List<Square> diagonal, List<Square> allowedMoves) throws BoardServiceException {
    findBeatenMovesOnHalfDiagonal(beatenMoves, allowedMoves, diagonal, selectedSquare, down, queen);
    findBeatenMovesOnHalfDiagonal(beatenMoves, allowedMoves, diagonal, selectedSquare, !down, queen);
  }

  private void findBeatenMovesOnHalfDiagonal(List<Square> beatenMoves, List<Square> allowedMoves, List<Square> diagonal, Square selectedSquare, boolean down, boolean queen) throws BoardServiceException {
    findBeatenMovesOnHalfDiagonal(beatenMoves, allowedMoves, diagonal, selectedSquare, down, queen, 0);
  }

  private void findBeatenMovesOnHalfDiagonal(List<Square> beatenMoves, List<Square> allowedMoves, List<Square> diagonal, Square selectedSquare, boolean down, boolean queen, int deep) throws BoardServiceException {
    int indexOfSelected = diagonal.indexOf(selectedSquare);
    ListIterator<Square> squareListIterator = diagonal.listIterator(indexOfSelected);
    Square next, previous = selectedSquare;
    deep++;
    boolean mustBeat;
    do {
      if (/*!beatenMoves.isEmpty() && !queen || */!((down && squareListIterator.hasNext()) || (!down && squareListIterator.hasPrevious()))) {
        break;
      }
      next = down ? squareListIterator.next() : squareListIterator.previous();
      mustBeat = mustBeat(next, previous);
      if (mustBeat) {
        // turk stroke
        if (previous.getDraught().isBeaten()) {
          return;
        }
        previous.getDraught().setBeaten(true);
        beatenMoves.add(previous);
        allowedMoves.add(next);
        if (!queen) {
          walkCrossDiagonal(beatenMoves, allowedMoves, next, previous, down, deep, false);
        }
      } else if (isDraughtWithSameColor(next)) {
        return;
      }
      if (!beatenMoves.isEmpty() && deep < 3 && queen) {
//        List<Square> newBeaten = new ArrayList<>();
        walkCrossDiagonal(beatenMoves, allowedMoves, next, previous, down, deep, true);
        if (!beatenMoves.isEmpty() && deep < 2) {
          allowedMoves.add(next);
        }
//        beatenMoves.addAll(newBeaten);
      }
      previous = next;
    }
    while ((down && squareListIterator.hasNext()) || (!down && squareListIterator.hasPrevious()));
  }

  private void walkCrossDiagonal(List<Square> beatenMoves, List<Square> allowedMoves, Square next, Square previous, boolean down, int deep, boolean queen) throws BoardServiceException {
    for (List<Square> diagonal : next.getDiagonals()) {
      if (!isSubDiagonal(diagonal, Arrays.asList(previous, next))) {
        findBeatenMovesOnHalfDiagonal(beatenMoves, allowedMoves, diagonal, next, down, queen, deep);
        findBeatenMovesOnHalfDiagonal(beatenMoves, allowedMoves, diagonal, next, !down, queen, deep);
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
