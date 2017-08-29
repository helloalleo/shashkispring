package com.workingbit.board.service;

import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.workingbit.board.common.EnumSearch.allowed;
import static com.workingbit.board.common.EnumSearch.beaten;

/**
 * Created by Aleksey Popryaduhin on 19:39 10/08/2017.
 */
public class HighlightMoveUtil {

  /**
   * possible directions of moving
   */
  private BoardContainer board;
  private Square selectedSquare;
  private EnumRules rules;

  private HighlightMoveUtil(BoardContainer board, Square selectedSquare, EnumRules rules) throws BoardServiceException {
    if (selectedSquare == null || selectedSquare.getDraught() == null) {
      throw new BoardServiceException("Selected square without placed draught");
    }
    this.board = board;
    this.selectedSquare = selectedSquare;
    this.rules = rules;
    selectedSquare.getDraught().setHighlighted(true);
    board.setSelectedSquare(selectedSquare);
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
    boolean queen = draught.isQueen();
    findBeatenMoves(selectedSquare, allowedMoves, beatenMoves, black, queen, 0);
    if (beatenMoves.isEmpty()) {
      findAllowedMoves(selectedSquare, allowedMoves, black, queen);
    }
    Map<String, Object> allowedAndBeatenMap = new HashMap<>();
    allowedAndBeatenMap.put(allowed.name(), allowedMoves);
    allowedAndBeatenMap.put(beaten.name(), beatenMoves);
    return allowedAndBeatenMap;
  }

  private void findAllowedMoves(Square selectedSquare, List<Square> allowedMoves, boolean black, boolean queen) {
    Set<List<Square>> diagonals = selectedSquare.getDiagonals();
    for (List<Square> diagonal : diagonals) {
      int indexOfSelected = diagonal.indexOf(selectedSquare);
      if (indexOfSelected != -1) {
        ListIterator<Square> squareListIterator = diagonal.listIterator(indexOfSelected);
        if (!queen) {
          findAllowed(selectedSquare, allowedMoves, black, squareListIterator);
        } else {
          findAllowedForQueen(selectedSquare, allowedMoves, black, squareListIterator);
          squareListIterator = diagonal.listIterator(indexOfSelected);
          findAllowedForQueen(selectedSquare, allowedMoves, !black, squareListIterator);
        }
      }
    }
  }

  private void findAllowedForQueen(Square selectedSquare, List<Square> allowedMoves, boolean black, ListIterator<Square> squareListIterator) {
    do {
      findAllowed(selectedSquare, allowedMoves, black, squareListIterator);
    } while (black && squareListIterator.hasNext() || !black && squareListIterator.hasPrevious());
  }

  private void findAllowed(Square selectedSquare, List<Square> allowedMoves, boolean black, ListIterator<Square> squareListIterator) {
    Square next = black ? squareListIterator.next() : squareListIterator.previous();
    if (canMove(selectedSquare, next)) {
      allowedMoves.add(next);
    }
  }

  private void findBeatenMoves(Square selectedSquare, List<Square> allowedMoves, List<Square> beatenMoves, boolean black, boolean queen, int deep) throws BoardServiceException {
    Set<List<Square>> diagonals = selectedSquare.getDiagonals();
    for (List<Square> squares : diagonals) {
      int indexOfSelected = squares.indexOf(selectedSquare);
      if (indexOfSelected != -1) {
        ListIterator<Square> squareListIterator = squares.listIterator(indexOfSelected);
        findBeaten(allowedMoves, beatenMoves, squareListIterator, selectedSquare, black, queen, deep);
        squareListIterator = squares.listIterator(indexOfSelected);
        findBeaten(allowedMoves, beatenMoves, squareListIterator, selectedSquare, !black, queen, deep);
      }
    }
  }

  private void findBeaten(List<Square> allowedMoves, List<Square> beatenMoves, ListIterator<Square> squareListIterator, Square selectedSquare, boolean black, boolean queen, int deep) throws BoardServiceException {
    Square next, previous = selectedSquare;
    boolean mustBeat;
    do {
      if (!((black && squareListIterator.hasNext()) || (!black && squareListIterator.hasPrevious()))) {
        break;
      }
      next = black ? squareListIterator.next() : squareListIterator.previous();
      mustBeat = mustBeat(next, previous);
      if (mustBeat ) {
        if (beatenMoves.contains(previous)) {
          return;
        }
        beatenMoves.add(previous);
        deep++;
        findBeatenMoves(next, allowedMoves, beatenMoves, black, queen, deep);
        if (queen) {
          if (black) {
            squareListIterator.previous();
          } else {
            squareListIterator.next();
          }
          findAllowedForQueen(previous, allowedMoves, black, squareListIterator);
        } else {
          allowedMoves.add(next);
        }
      } else if (isDraughtWithSameColor(next)) {
        return;
      }
      previous = next;
    } while ((black && squareListIterator.hasNext()) || (!black && squareListIterator.hasPrevious()));
  }

  private boolean isDraughtWithSameColor(Square next) {
    return next.isOccupied() && next.getDraught().isBlack() == this.selectedSquare.getDraught().isBlack();
  }

  private boolean canMove(Square selectedSquare, Square currentSquare) {
    boolean beaten = currentSquare.getDraught() != null && currentSquare.getDraught().isBeaten();
    return !currentSquare.isOccupied() && !beaten;
  }

  /**
   * Return allowed square for move after beat
   *
   * @param nextSquare
   * @return
   */
  private boolean mustBeat(Square nextSquare, Square previousSquare) throws BoardServiceException {
    if (previousSquare.isOccupied()
        && previousSquare.getDraught().isBlack() != this.selectedSquare.getDraught().isBlack()
        && !nextSquare.isOccupied()) {
      previousSquare.getDraught().setBeaten(true);
      return true;
    }
    return false;
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
