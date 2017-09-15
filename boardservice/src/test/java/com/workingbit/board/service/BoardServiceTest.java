package com.workingbit.board.service;

import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;
import com.workingbit.share.model.CreateBoardRequest;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.workingbit.board.common.EnumBaseKeys.*;
import static com.workingbit.board.common.EnumSearch.allowed;
import static com.workingbit.board.common.EnumSearch.beaten;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Aleksey Popryaduhin on 10:08 10/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BoardServiceTest extends BaseServiceTest {

  @Test
  public void createBoard() throws Exception {
    BoardContainer boardContainer = boardService.createBoard(getCreateBoardRequest());
    toDelete(boardContainer);
    assertNotNull(boardContainer.getId());
  }

  @Test
  public void findAll() throws Exception {
    BoardContainer board = getNewBoard();
    toDelete(board);
    assertNotNull(board.getId());
    List<BoardContainer> all = boardService.findAll(null);
    assertTrue(all.contains(board));
  }

  @Test
  public void findById() throws Exception {
    BoardContainer board = getNewBoard();
    toDelete(board);
    assertNotNull(board.getId());
    Optional<BoardContainer> byId = boardService.findById(board.getId());
    assertNotNull(byId.get());
  }

  @Test
  public void delete() throws Exception {
    BoardContainer board = getNewBoard();
    String boardId = board.getId();
    assertNotNull(boardId);
    boardService.delete(boardId);
    Optional<BoardContainer> byId = boardService.findById(boardId);
    assertTrue(!byId.isPresent());
  }

  /*
  @Test
  public void should_save_move_history() throws BoardServiceException, ExecutionException, InterruptedException {
    BoardContainer board = getNewBoard();
    Draught draught = getDraught(5, 2);
    Square square = getSquareByVH(board, 5, 2);
    square.setDraught(draught);
    Square target = getSquareByVH(board, 4, 3);

    // find allowed and beaten
//    HighlightMoveService highlightMoveUtil = new HighlightMoveService(board.getCurrentBoard(), square, getRules());
//    Optional<List<Square>> allowedMovesMap = HighlightMoveService.highlight(board,square);
//    List<Square> allowedMoves = (List<Square>) allowedMovesMap.get(allowed.name());
//    List<Draught> beatenMoves = (List<Draught>) allowedMovesMap.get(beaten.name());

    // create moveTo action
    BoardContainer finalBoard = board;
    Map<String, Object> moveTo = new HashMap<String, Object>() {{
      put(boardId.name(), finalBoard.getId());
      put(selectedSquare.name(), square);
      put(targetSquare.name(), target);
//      put(allowed.name(), allowedMoves);
//      put(beaten.name(), beatenMoves);
    }};

    // move draught and save
    Map<String, Object> newMoveCoords = boardService.move(moveTo);

    // find saved and check if it's selected square is equals to target
    board = boardService.findById(board.getId()).get();
//    Square newSelectedDraught = board.getSelectedSquare();
//    assertEquals(target, newSelectedDraught);

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
    BoardContainer board = getNewBoard();
    Square square = getSquareByVH(board, 5, 2);
    Square target = getSquareByVH(board, 4, 3);

//    Map<String, Object> highlight = boardService.highlight(boardId, square);
    // find allowed and beaten
//    List<Square> allowedMoves = (List<Square>) highlight.get(allowed.name());
//    List<Draught> beatenMoves = (List<Draught>) highlight.get(beaten.name());

    // create moveTo action
    Map<String, Object> moveTo = getMoveTo(board, square, target, null, null);
    MapUtils.debugPrint(System.out, "PREP MOVE", moveTo);

    // move draught and save
    Map<String, Object> newMoveCoords = boardService.move(moveTo);
    MapUtils.debugPrint(System.out, "MOVE", newMoveCoords);

    // next move
    Object newSource = newMoveCoords.get(EnumBaseKeys.targetSquare.name());
//    highlight = boardService.highlight(boardId, newSource);
    // find allowed and beaten
//    allowedMoves = (List<Square>) highlight.get(allowed.name());
//    beatenMoves = (List<Draught>) highlight.get(beaten.name());

    Square nextTarget = BoardUtils.findSquareByVH(board, 3,4).get();
    // create moveTo action
    moveTo = getMoveTo(board, target, nextTarget, null, null);
    MapUtils.debugPrint(System.out, "PREP MOVE", moveTo);

    // move draught and save
    newMoveCoords = boardService.move(moveTo);
    MapUtils.debugPrint(System.out, "MOVE", newMoveCoords);

    Map<String, Object> undo = boardHistoryService.undo(board.getId());
    MapUtils.debugPrint(System.out, "UNDO", undo);
  }
*/
  private HashMap<String, Object> getMoveTo(BoardContainer board, Square square, Square target, List<Square> allowedMoves, List<Draught> beatenMoves) {
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

  private List<BoardContainer> boards = new ArrayList<>();

  private void toDelete(BoardContainer board) {
    boards.add(board);
  }

  private BoardContainer getNewBoard() {
    CreateBoardRequest createBoardRequest = getCreateBoardRequest();
    BoardContainer board = boardService.createBoard(createBoardRequest);

    // place initial draught on the desk
//    Draught draught = getDraught(5, 2);
//    Optional<Square> sel = BoardUtils.findSquareByVH(board.getCurrentBoard(), 5, 2);
//    Square square = sel.get();
//    square.setDraught(draught);
//    board.getCurrentBoard().setSelectedSquare(square);
//    boardDao.save(board);
    return board;
  }
}