package com.workingbit.board.service;

import com.workingbit.board.exception.BoardServiceError;
import com.workingbit.share.domain.impl.BoardBox;
import com.workingbit.share.domain.impl.Draught;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Aleksey Popryaduhin on 21:13 11/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MoveUtilTest extends BaseServiceTest {

  @Test
  public void doMove() throws Exception, BoardServiceError {
    BoardBox board = getBoard(false);
    Draught draught = getDraught(5, 2);
//    ISquare square = getSquareByVH(board, 5, 2);
//    square.setDraught(draught);
//    ISquare target = getSquareByVH(board, 4, 3);
//    HighlightMoveService highlightMoveService = new HighlightMoveService(board, square, getRules());
//    Map<String, Object> allowedMovesMap = highlightMoveService.findAllowedMoves();
//    List<ISquare> allowedMoves = (List<ISquare>) allowedMovesMap.get(allowed.name());
//    List<IDraught> beatenMoves = (List<IDraught>) allowedMovesMap.get(beaten.name());
//    MoveUtil moveService = MoveUtil.getService(board, square, target, allowedMoves, beatenMoves);
//    Map<String, Object> move = moveService.move();
//    MapUtils.debugPrint(System.out, "Move", move);
//    int vMove = (int) move.get(v.name());
//    int hMove = (int) move.get(h.name());
//    boolean queenMove = (boolean) move.get(queen.name());

  }
}