package com.workingbit.board.service;

import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.domain.IBoard;
import com.workingbit.share.domain.IDraught;
import com.workingbit.share.domain.ISquare;
import com.workingbit.share.domain.impl.Draught;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.workingbit.board.common.EnumSearch.allowed;
import static com.workingbit.board.common.EnumSearch.beaten;

/**
 * Created by Aleksey Popryaduhin on 21:13 11/08/2017.
 */
public class MoveServiceTest extends BaseServiceTest {

  @Test
  public void doMove() throws Exception, BoardServiceException {
    IBoard board = getBoard();
    Draught draught = getDraught(5, 2);
    ISquare square = getSquareByVH(board, 5, 2);
    square.setDraught(draught);
    ISquare target = getSquareByVH(board, 4, 3);
    HighlightMoveService highlightMoveService = new HighlightMoveService(board, square);
    Map<String, Object> allowedMovesMap = highlightMoveService.findAllowedMoves();
    List<ISquare> allowedMoves = (List<ISquare>) allowedMovesMap.get(allowed.name());
    List<IDraught> beatenMoves = (List<IDraught>) allowedMovesMap.get(beaten.name());
    MoveService moveService = MoveService.getService(board, square, target, allowedMoves, beatenMoves);
    Map<String, Object> move = moveService.doMove();
//    MapUtils.debugPrint(System.out, "Move", move);
//    int vMove = (int) move.get(v.name());
//    int hMove = (int) move.get(h.name());
//    boolean queenMove = (boolean) move.get(queen.name());

  }
}