package com.workingbit.board.service;

import com.workingbit.board.common.EnumBaseKeys;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.NewBoardRequest;
import com.workingbit.share.domain.impl.Square;
import org.apache.commons.collections4.MapUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static com.workingbit.board.common.EnumBaseKeys.*;
import static com.workingbit.board.common.EnumSearch.allowed;
import static com.workingbit.board.common.EnumSearch.beaten;
import static org.junit.Assert.*;

/**
 * Created by Aleksey Popryaduhin on 10:08 10/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BoardServiceTest extends BaseServiceTest {

  @Test
  public void createBoard() throws Exception {
    Board board = getNewBoard();
    toDelete(board);
    assertNotNull(board.getId());
  }

  @Test
  public void findAll() throws Exception {
    Board board = getNewBoard();
    toDelete(board);
    assertNotNull(board.getId());
    List<Board> all = boardService.findAll();
    assertTrue(all.contains(board));
  }

  @Test
  public void findById() throws Exception {
    Board board = getNewBoard();
    toDelete(board);
    assertNotNull(board.getId());
    Optional<Board> byId = boardService.findById(board.getId());
    assertNotNull(byId.get());
  }

  @Test
  public void delete() throws Exception {
    Board board = getNewBoard();
    String boardId = board.getId();
    assertNotNull(boardId);
    boardService.delete(boardId);
    Optional<Board> byId = boardService.findById(boardId);
    assertTrue(!byId.isPresent());
  }

  @Test
  public void should_save_move_history() throws BoardServiceException {
    Board board = getNewBoard();
    Draught draught = getDraught(5, 2);
    Square square = getSquareByVH(board.getCurrentBoard(), 5, 2);
    square.setDraught(draught);
    Square target = getSquareByVH(board.getCurrentBoard(), 4, 3);

    // find allowed and beaten
//    HighlightMoveUtil highlightMoveUtil = new HighlightMoveUtil(board.getCurrentBoard(), square, getRules());
    Map<String, Object> allowedMovesMap = HighlightMoveUtil.highlight(board,square);
    List<Square> allowedMoves = (List<Square>) allowedMovesMap.get(allowed.name());
    List<Draught> beatenMoves = (List<Draught>) allowedMovesMap.get(beaten.name());

    // create moveTo action
    Board finalBoard = board;
    Map<String, Object> moveTo = new HashMap<String, Object>() {{
      put(boardId.name(), finalBoard.getId());
      put(selectedSquare.name(), square);
      put(targetSquare.name(), target);
      put(allowed.name(), allowedMoves);
      put(beaten.name(), beatenMoves);
    }};

    // move draught and save
    Map<String, Object> newMoveCoords = boardService.move(moveTo);

    // find saved and check if it's selected square is equals to target
    board = boardService.findById(board.getId()).get();
    Square newSelectedDraught = board.getCurrentBoard().getSelectedSquare();
    assertEquals(target, newSelectedDraught);

    assertEquals(newMoveCoords.get(v.name()), -60); // v - up
    assertEquals(newMoveCoords.get(h.name()), 60); // h - right

    // undo and get new board with new board container
    Map<String, Object> undoneBoard = boardHistoryService.undo(board.getId());
//    assertTrue(undoneBoard.isPresent());
//    Square oldSelectedDraught = undoneBoard.get().getCurrentBoard().getSelectedSquare();
//    assertEquals(square, oldSelectedDraught);
  }

  @Test
  public void should_undo_move() throws BoardServiceException {
    Board board = getNewBoard();
    Square square = getSquareByVH(board.getCurrentBoard(), 5, 2);
    Square target = getSquareByVH(board.getCurrentBoard(), 4, 3);

    Map<String, Object> hl = new HashMap<String, Object>() {{
      put(selectedSquare.name(), square);
      put(boardId.name(), board.getId());
    }};
    Map<String, Object> highlight = boardService.highlight(hl);
    // find allowed and beaten
    List<Square> allowedMoves = (List<Square>) highlight.get(allowed.name());
    List<Draught> beatenMoves = (List<Draught>) highlight.get(beaten.name());

    // create moveTo action
    Map<String, Object> moveTo = getMoveTo(board, square, target, allowedMoves, beatenMoves);
    MapUtils.debugPrint(System.out, "PREP MOVE", moveTo);

    // move draught and save
    Map<String, Object> newMoveCoords = boardService.move(moveTo);
    MapUtils.debugPrint(System.out, "MOVE", newMoveCoords);

    // next move
    Object newSource = newMoveCoords.get(EnumBaseKeys.targetSquare.name());
    hl = new HashMap<String, Object>() {{
      put(selectedSquare.name(), newSource);
      put(boardId.name(), board.getId());
    }};
    highlight = boardService.highlight(hl);
    // find allowed and beaten
    allowedMoves = (List<Square>) highlight.get(allowed.name());
    beatenMoves = (List<Draught>) highlight.get(beaten.name());

    Square nextTarget = BoardUtils.findSquareByVH(board.getCurrentBoard(), 3,4).get();
    // create moveTo action
    moveTo = getMoveTo(board, target, nextTarget, allowedMoves, beatenMoves);
    MapUtils.debugPrint(System.out, "PREP MOVE", moveTo);

    // move draught and save
    newMoveCoords = boardService.move(moveTo);
    MapUtils.debugPrint(System.out, "MOVE", newMoveCoords);

    Map<String, Object> undo = boardHistoryService.undo(board.getId());
    MapUtils.debugPrint(System.out, "UNDO", undo);
  }

  private HashMap<String, Object> getMoveTo(Board board, Square square, Square target, List<Square> allowedMoves, List<Draught> beatenMoves) {
    return new HashMap<String, Object>() {{
      put(boardId.name(), board.getId());
      put(selectedSquare.name(), square);
      put(targetSquare.name(), target);
      put(allowed.name(), allowedMoves);
      put(beaten.name(), beatenMoves);
    }};
  }

  @After
  public void tearUp() {
    boards.forEach(board -> boardService.delete(board.getId()));
  }

  private List<Board> boards = new ArrayList<>();

  private void toDelete(Board board) {
    boards.add(board);
  }

  private Board getNewBoard() {
    NewBoardRequest newBoardRequest = new NewBoardRequest(false,false, EnumRules.RUSSIAN, 40);
    Board board = boardService.createBoard(newBoardRequest);

    // place initial draught on the desk
    Draught draught = getDraught(5, 2);
    Optional<Square> sel = BoardUtils.findSquareByVH(board.getCurrentBoard(), 5, 2);
    Square square = sel.get();
    square.setDraught(draught);
//    board.getCurrentBoard().setSelectedSquare(square);
    boardDao.save(board);
    return board;
  }
}