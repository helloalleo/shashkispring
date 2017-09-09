package com.workingbit.board.service;

import com.workingbit.board.common.EnumBaseKeys;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.workingbit.board.common.EnumBaseKeys.*;
import static com.workingbit.board.service.BoardUtils.getBoardServiceExceptionSupplier;
import static com.workingbit.board.service.BoardUtils.getDistanceVH;

/**
 * Created by Aleksey Popryaduhin on 20:14 11/08/2017.
 */
public class MoveUtil {
  private final BoardContainer board;
  private final List<Square> allowedMoves;
  private final List<Draught> beatenMoves;
  private final Square sourceSquare;
  private final Square targetSquare;
  private final boolean undo;

  MoveUtil(BoardContainer board, Square sourceSquare, Square targetSquare, List<Square> allowedMoves, List<Draught> beatenMoves, boolean undo) throws BoardServiceException {
    /*
     */
    if (!undo && !allowedMoves.contains(targetSquare)) {
      throw new BoardServiceException("Move not allowed");
    }
    this.undo = undo;
    this.board = board;
    this.sourceSquare = BoardUtils.findSquareLink(getBoardContainer(), sourceSquare).orElseThrow(getBoardServiceExceptionSupplier("Source square not found"));
    this.targetSquare = BoardUtils.findSquareLink(getBoardContainer(), targetSquare).orElseThrow(getBoardServiceExceptionSupplier("Target square not found"));
    this.allowedMoves = allowedMoves;
    this.beatenMoves = beatenMoves;
  }

  public BoardContainer getBoardContainer() {
    return board;
  }

  /**
   * Moves draught to new target and set board's selected square
   * @return Pair of updated board and date:
   *        {moveDist: {v, h, queen}, targetSquare: Square} v - distance for moving vertical (minus up),
   *        h - distance for move horizontal (minus left), targetSquare is a new square with
   *        moved draught, queen is a draught has become the queen
   */
  public Pair<BoardContainer, Map<String, Object>> moveAndUpdateBoard() {
    return Pair.of(getBoardContainer(), moveDraught());
  }

  private Map<String, Object> moveDraught() {
    targetSquare.setDraught(sourceSquare.getDraught());
    targetSquare.getDraught().setV(targetSquare.getV());
    targetSquare.getDraught().setH(targetSquare.getH());
    targetSquare.getDraught().setHighlighted(true);
    sourceSquare.setDraught(null);
//    getBoardContainer().setSelectedSquare((Square) targetSquare);
    Pair<Integer, Integer> distanceVH = getDistanceVH(sourceSquare, targetSquare);
    int vMove = distanceVH.getLeft() * sourceSquare.getSize();
    int hMove = distanceVH.getRight() * sourceSquare.getSize();
//    getBoardContainer().setSelectedSquare(targetSquare);
    return new HashMap<String, Object>() {{
      put(moveDist.name(), new HashMap<String, Object>() {{
        put(v.name(), vMove);
        put(h.name(), hMove);
        put(queen.name(), targetSquare.getDraught().isQueen());
      }});
      put(undoMove.name(), undo);
      put(EnumBaseKeys.selectedSquare.name(), sourceSquare);
      put(EnumBaseKeys.targetSquare.name(), targetSquare);
    }};
  }
}
