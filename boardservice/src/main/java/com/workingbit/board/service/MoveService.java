package com.workingbit.board.service;

import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.domain.IBoardContainer;
import com.workingbit.share.domain.IDraught;
import com.workingbit.share.domain.ISquare;
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
public class MoveService {
  private final IBoardContainer board;
  private final List<ISquare> allowedMoves;
  private final List<IDraught> beatenMoves;
  private final ISquare sourceSquare;
  private final ISquare targetSquare;

  MoveService(IBoardContainer board, ISquare sourceSquare, ISquare targetSquare, List<ISquare> allowedMoves, List<IDraught> beatenMoves) throws BoardServiceException {
    /*
     */
    if (!allowedMoves.contains(targetSquare)) {
      throw new BoardServiceException("Move not allowed");
    }
    this.board = board;
    this.sourceSquare = BoardUtils.findSquareLink(board, sourceSquare).orElseThrow(getBoardServiceExceptionSupplier("Source square not found"));
    this.targetSquare = BoardUtils.findSquareLink(board, targetSquare).orElseThrow(getBoardServiceExceptionSupplier("Target square not found"));
    this.allowedMoves = allowedMoves;
    this.beatenMoves = beatenMoves;
  }

  public static MoveService getService(IBoardContainer board, ISquare sourceSquare, ISquare targetSquare, List<ISquare> allowedMoves, List<IDraught> beatenMoves) throws BoardServiceException {
    return new MoveService(board, sourceSquare, targetSquare, allowedMoves, beatenMoves);
  }

  public Map<String, Object> doMove() {
    return moveDraught();
  }

  public void saveBoard(BoardService boardService) {
    boardService.save(board);
  }

  private Map<String, Object> moveDraught() {
    targetSquare.setDraught(sourceSquare.getDraught());
    targetSquare.getDraught().setV(targetSquare.getV());
    targetSquare.getDraught().setH(targetSquare.getH());
    targetSquare.getDraught().setHighlighted(true);
    sourceSquare.setDraught(null);
    Pair<Integer, Integer> distanceVH = getDistanceVH(sourceSquare, targetSquare);
    int vMove = distanceVH.getLeft() * sourceSquare.getSize();
    int hMove = distanceVH.getRight() * sourceSquare.getSize();
    return new HashMap<String, Object>() {{
      put(v.name(), vMove);
      put(h.name(), hMove);
      put(queen.name(), targetSquare.getDraught().isQueen());
    }};
  }
}
