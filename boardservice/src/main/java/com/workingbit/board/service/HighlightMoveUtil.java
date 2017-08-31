package com.workingbit.board.service;

import com.github.rutledgepaulv.prune.Tree;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.common.Utils;
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

  static Optional<Map<String, Object>> highlight(BoardContainer board, Square selectedSquare) throws BoardServiceException, ExecutionException, InterruptedException {
    try {
      // highlight moves for the selected square
      HighlightMoveUtil highlightMoveUtil = new HighlightMoveUtil(board, selectedSquare);
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
    for (List<Square> diagonal : diagonals) {
      indexOfSelected = diagonal.indexOf(selectedSquare);
      if (indexOfSelected != -1) {
        walkOnDiagonal(selectedSquare, black, queen, diagonal, beatenMoves, allowedMoves);
      }
    }
  }

  private void walkOnDiagonal(Square selectedSquare, boolean down, boolean queen, List<Square> diagonal, List<Square> beatenMoves, List<Square> allowedMoves) throws BoardServiceException {
    Tree<Square> treeBeaten = Tree.empty();
    findBeatenMovesOnHalfDiagonal(Utils.cloneList(diagonal), selectedSquare, down, queen, 0, true, treeBeaten.asNode(), allowedMoves);
    beatenMoves.addAll(flatTree(treeBeaten));
    treeBeaten = Tree.empty();
    findBeatenMovesOnHalfDiagonal(Utils.cloneList(diagonal), selectedSquare, !down, queen, 0, true, treeBeaten.asNode(), allowedMoves);
    beatenMoves.addAll(flatTree(treeBeaten));
  }

  private List<Square> flatTree(Tree<Square> treeBeaten) {
    return treeBeaten.breadthFirstStream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
  }

  private void findBeatenMovesOnHalfDiagonal(List<Square> diagonal, Square selectedSquare, boolean down, boolean queen, int deep, boolean cross, Tree.Node<Square> beatenMoves, List<Square> allowedMoves) throws BoardServiceException {
    if (queen) {
      findBeatenMovesForQueen(diagonal, selectedSquare, down, deep, cross, beatenMoves, allowedMoves);
    } else {
      findBeatenMovesForDraught(diagonal, selectedSquare, down, deep, beatenMoves, allowedMoves);
    }
  }

  private void findBeatenMovesForQueen(List<Square> diagonal, Square selectedSquare, boolean down, int deep, boolean cross, Tree.Node<Square> beatenMoves, List<Square> allowedMoves) throws BoardServiceException {
    int indexOfSelected = diagonal.indexOf(selectedSquare);
    ListIterator<Square> squareListIterator = diagonal.listIterator(indexOfSelected);
    List<Square> walkAllowedMoves = new ArrayList<>();
    Square next, previous = selectedSquare;
    deep++;
    boolean mustBeat;
    do {
      if (!((down && squareListIterator.hasNext()) || (!down && squareListIterator.hasPrevious()))) {
        break;
      }
      next = down ? squareListIterator.next() : squareListIterator.previous();
      mustBeat = mustBeat(next, previous);
      if (mustBeat) {
        if (treeContains(beatenMoves, previous)) {
          return;
        }
        addBeatenMove(beatenMoves, previous);
        cross = true;
      } else if (isDraughtWithSameColor(next)) {
        return;
      }
      if (!beatenMoves.getChildren().isEmpty() && canMove(next)) {
        if (cross) {
          walkCross(down, deep, beatenMoves, allowedMoves, walkAllowedMoves, next, previous);
        }
      }
      previous = next;
    }
    while ((down && squareListIterator.hasNext()) || (!down && squareListIterator.hasPrevious()));

    if (!walkAllowedMoves.isEmpty() && walkAllowedMoves.contains(previous)) {
      allowedMoves.addAll(walkAllowedMoves);
    }
  }

  private void addBeatenMove(Tree.Node<Square> beatenMoves, Square previous) {
    previous.getDraught().setBeaten(true);
    beatenMoves.addChild(previous);
  }

  private void walkCross(boolean down, int deep, Tree.Node<Square> beatenMoves, List<Square> allowedMoves, List<Square> walkAllowedMoves, Square next, Square previous) throws BoardServiceException {
    List<Tree.Node<Square>> children = beatenMoves.getChildren();
    Tree.Node<Square> newBeatenMoves = children.get(children.size() - 1);
    walkCrossDiagonalForBeaten(next, previous, down, deep, true, newBeatenMoves, allowedMoves);
    boolean hasBeatenOnCrossDiagonal = hasBeatenOnCrossDiagonal(next, previous);
    if (hasBeatenOnCrossDiagonal) {
      allowedMoves.add(next);
    } else if (newBeatenMoves.getChildren().isEmpty()) {
      walkAllowedMoves.add(next);
    }
  }

  private void findBeatenMovesForDraught(List<Square> diagonal, Square selectedSquare, boolean down, int deep, Tree.Node<Square> beatenMoves, List<Square> allowedMoves) throws BoardServiceException {
    int indexOfSelected = diagonal.indexOf(selectedSquare);
    ListIterator<Square> squareListIterator = diagonal.listIterator(indexOfSelected);
    Square next, previous = selectedSquare;
    deep++;
    int moveCounter = 0;
    boolean mustBeat;
    do {
      if (!((down && squareListIterator.hasNext()) || (!down && squareListIterator.hasPrevious()))) {
        break;
      }
      next = down ? squareListIterator.next() : squareListIterator.previous();
      mustBeat = mustBeat(next, previous);
      if (mustBeat) {
        if (leavesContain(beatenMoves, previous)) {
          return;
        }
        addBeatenMove(beatenMoves, previous);
        if (!allowedMoves.contains(next)) {
          allowedMoves.add(next);
        }
        walkCrossDiagonalForBeaten(next, previous, down, deep, false, beatenMoves, allowedMoves);
      } else if (isDraughtWithSameColor(next)) {
        return;
      }
      if (moveCounter > 0 && !down || moveCounter > 1) {
        return;
      }
      moveCounter++;
      previous = next;
    }
    while ((down && squareListIterator.hasNext()) || (!down && squareListIterator.hasPrevious()));
  }

  private boolean hasBeatenOnCrossDiagonal(Square next, Square previous) {
    for (List<Square> diagonal : next.getDiagonals()) {
      if (!isSubDiagonal(diagonal, Arrays.asList(previous, next))) {
        return diagonal.stream().anyMatch(square -> square.isOccupied() && square.getDraught().isBeaten());
      }
    }
    return false;
  }

  private boolean leavesContain(Tree.Node<Square> beatenMoves, Square search) {
    return beatenMoves.asTree().getLeaves().anyMatch(search::equals);
  }

  private boolean treeContains(Tree.Node<Square> beatenMoves, Square search) {
    Tree.Node<Square> squareNode = beatenMoves;
    while (squareNode.getParent().isPresent()) {
      if (squareNode.getData().equals(search)) {
        return true;
      }
      squareNode = squareNode.getParent().get();
    }
    return false;
  }

  private void walkCrossDiagonalForBeaten(Square next, Square previous, boolean down, int deep, boolean queen, Tree.Node<Square> beatenMoves, List<Square> allowedMoves) throws BoardServiceException {
    for (List<Square> diagonal : next.getDiagonals()) {
      if (!isSubDiagonal(diagonal, Arrays.asList(previous, next))) {
        findBeatenMovesOnHalfDiagonal(Utils.cloneList(diagonal), next, down, queen, deep, false, beatenMoves, allowedMoves);
        findBeatenMovesOnHalfDiagonal(Utils.cloneList(diagonal), next, !down, queen, deep, false, beatenMoves, allowedMoves);
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
