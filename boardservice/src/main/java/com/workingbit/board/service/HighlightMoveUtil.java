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
import static com.workingbit.board.service.BoardUtils.isSubDiagonal;

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
    findBeatenMovesOnDiagonalsOfSelectedSquare(selectedSquare, allowedMoves, beatenMoves, black, queen, 0);
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

  private void findBeatenMovesOnDiagonalsOfSelectedSquare(Square selectedSquare, List<Square> allowedMoves, List<Square> beatenMoves, boolean black, boolean queen, int deep) throws BoardServiceException {
    List<List<Square>> diagonals = selectedSquare.getDiagonals();
    int indexOfSelected;
    for (List<Square> diagonal : diagonals) {
      indexOfSelected = diagonal.indexOf(selectedSquare);
      if (indexOfSelected != -1) {
        List<Square> allowedForDiagonal = new ArrayList<>();
        List<Square> newBeaten = new ArrayList<>();
        walkOnDiagonal(selectedSquare, newBeaten, black, queen, deep, diagonal, allowedForDiagonal);

//        for (Square allowed : new ArrayList<>(allowedForDiagonal)) {
//          for (List<Square> secondDiagonal : allowed.getDiagonals()) {
//            indexOfSelected = secondDiagonal.indexOf(allowed);
//            if (!isSubDiagonal(diagonal, secondDiagonal)) {
//              walkOnDiagonal(allowed, beatenMoves, black, queen, deep, secondDiagonal, allowedForDiagonal);
//            }
//          }
//        }
        allowedMoves.addAll(allowedForDiagonal);
        beatenMoves.addAll(newBeaten);
      }
    }
  }

  private void walkOnDiagonal(Square selectedSquare, List<Square> beatenMoves, boolean down, boolean queen, int deep, List<Square> diagonal, List<Square> allowedForDiagonal) throws BoardServiceException {
    ListIterator<Square> squareListIterator;
    int indexOfSelected = diagonal.indexOf(selectedSquare);
    squareListIterator = diagonal.listIterator(indexOfSelected);
    if (!beatenMoves.isEmpty() && !isSubDiagonal(diagonal, Arrays.asList(selectedSquare, beatenMoves.get(beatenMoves.size() - 1)))) {
      findBeatenMovesOnHalfDiagonal(allowedForDiagonal, beatenMoves, squareListIterator, selectedSquare, down, queen, deep);
//      findBeatenMovesOnHalfDiagonal(allowedForDiagonal, beatenMoves, squareListIterator, selectedSquare, !down, queen, deep);
    } else if (beatenMoves.isEmpty()) {
      findBeatenMovesOnHalfDiagonal(allowedForDiagonal, beatenMoves, squareListIterator, selectedSquare, down, queen, deep);
//      findBeatenMovesOnHalfDiagonal(allowedForDiagonal, beatenMoves, squareListIterator, selectedSquare, !down, queen, deep);
    }
    squareListIterator = diagonal.listIterator(indexOfSelected);
    if (!beatenMoves.isEmpty() && !isSubDiagonal(diagonal, Arrays.asList(selectedSquare, beatenMoves.get(beatenMoves.size() - 1)))) {
      findBeatenMovesOnHalfDiagonal(allowedForDiagonal, beatenMoves, squareListIterator, selectedSquare, !down, queen, deep);
//      findBeatenMovesOnHalfDiagonal(allowedForDiagonal, beatenMoves, squareListIterator, selectedSquare, down, queen, deep);
    } else if (beatenMoves.isEmpty()) {
      findBeatenMovesOnHalfDiagonal(allowedForDiagonal, beatenMoves, squareListIterator, selectedSquare, !down, queen, deep);
//      findBeatenMovesOnHalfDiagonal(allowedForDiagonal, beatenMoves, squareListIterator, selectedSquare, down, queen, deep);
    }
  }

  private void findBeatenMovesOnHalfDiagonal(List<Square> allowedMoves, List<Square> beatenMoves, ListIterator<Square> squareListIterator, Square selectedSquare, boolean down, boolean queen, int deep) throws BoardServiceException {
    Square next, previous = selectedSquare;
    boolean mustBeat;
    do {
      if (!((down && squareListIterator.hasNext()) || (!down && squareListIterator.hasPrevious()))) {
        break;
      }
      next = down ? squareListIterator.next() : squareListIterator.previous();
      mustBeat = mustBeat(next, previous);
      if (mustBeat) {
        if (beatenMoves.contains(previous)) {
          System.out.println(previous);
          return;
        }
        beatenMoves.add(previous);
        deep++;
//        if (queen) {
//          if (down) {
//            squareListIterator.previous();
//          } else {
//            squareListIterator.next();
//          }
//          findAllowedForQueen(previous, allowedMoves, down, squareListIterator);
//        } else {
//          allowedMoves.add(next);
//        }
        allowedMoves.add(next);
      } else if (isDraughtWithSameColor(next)) {
        return;
      }
      if (!beatenMoves.isEmpty() && deep != -1) {
        for (List<Square> diagonal : next.getDiagonals()) {
          if (!isSubDiagonal(diagonal, Arrays.asList(previous, next))) {
            int indexOfSelected = diagonal.indexOf(next);
            ListIterator<Square> squareListIteratorDown = diagonal.listIterator(indexOfSelected);
            findBeatenMovesOnHalfDiagonal(allowedMoves, beatenMoves, squareListIteratorDown, next, down, queen, -1);
            ListIterator<Square> squareListIteratorUp = diagonal.listIterator(indexOfSelected);
            findBeatenMovesOnHalfDiagonal(allowedMoves, beatenMoves, squareListIteratorUp, next, !down, queen, -1);
          }
        }
      }
      previous = next;
    } while ((down && squareListIterator.hasNext()) || (!down && squareListIterator.hasPrevious()));
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

  private boolean isDown(Square previousSquare, Square selectedSquare) {
    return selectedSquare.getV() - previousSquare.getV() > 0;
  }
}
