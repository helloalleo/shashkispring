package com.workingbit.board.service;

import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.IBoard;
import com.workingbit.share.domain.IDraught;
import com.workingbit.share.domain.ISquare;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.NewBoardRequest;
import com.workingbit.share.domain.impl.Square;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static com.workingbit.board.common.EnumBaseKeys.*;
import static com.workingbit.board.common.EnumSearch.allowed;
import static com.workingbit.board.common.EnumSearch.beaten;
import static org.junit.Assert.assertEquals;
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
    IBoard board = getNewBoard();
    toDelete(board);
    assertNotNull(board.getId());
  }

  @Test
  public void findAll() throws Exception {
    IBoard board = getNewBoard();
    toDelete(board);
    assertNotNull(board.getId());
    List<Board> all = boardService.findAll();
    assertTrue(all.contains(board));
  }

  @Test
  public void findById() throws Exception {
    IBoard board = getNewBoard();
    toDelete(board);
    assertNotNull(board.getId());
    Optional<Board> byId = boardService.findById(board.getId());
    assertNotNull(byId.get());
  }

  @Test
  public void delete() throws Exception {
    IBoard board = getNewBoard();
    String boardId = board.getId();
    assertNotNull(boardId);
    boardService.delete(boardId);
    Optional<Board> byId = boardService.findById(boardId);
    assertTrue(!byId.isPresent());
  }

  @Test
  public void should_save_move_history() throws BoardServiceException {
    IBoard board = getNewBoard();
    Draught draught = getDraught(5, 2);
    ISquare square = getSquareByVH(board.getCurrentBoard(), 5, 2);
    square.setDraught(draught);
    ISquare target = getSquareByVH(board.getCurrentBoard(), 4, 3);

    // find allowed and beaten
    HighlightMoveService highlightMoveService = new HighlightMoveService(board.getCurrentBoard(), (Square) square, getRules());
    Map<String, Object> allowedMovesMap = highlightMoveService.findAllowedMoves();
    List<ISquare> allowedMoves = (List<ISquare>) allowedMovesMap.get(allowed.name());
    List<IDraught> beatenMoves = (List<IDraught>) allowedMovesMap.get(beaten.name());

    // create moveTo action
    Map<String, Object> moveTo = new HashMap<String, Object>() {{
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
    Optional<Board> undoneBoard = boardHistoryService.undo(board.getId());
    assertTrue(undoneBoard.isPresent());
    Square oldSelectedDraught = undoneBoard.get().getCurrentBoard().getSelectedSquare();
    assertEquals(square, oldSelectedDraught);
  }

  @After
  public void tearUp() {
    boards.forEach(board -> boardService.delete(board.getId()));
  }

  private List<IBoard> boards = new ArrayList<>();

  private void toDelete(IBoard board) {
    boards.add(board);
  }

  private IBoard getNewBoard() {
    NewBoardRequest newBoardRequest = new NewBoardRequest(true,false, EnumRules.RUSSIAN, 40);
    return boardService.createBoard(newBoardRequest);
  }
}