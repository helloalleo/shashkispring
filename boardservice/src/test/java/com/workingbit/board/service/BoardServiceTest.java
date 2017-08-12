package com.workingbit.board.service;

import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.IBoard;
import com.workingbit.share.domain.impl.NewBoardRequest;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Aleksey Popryaduhin on 10:08 10/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BoardServiceTest {

  @Autowired
  private BoardService boardService;

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
    List<IBoard> all = boardService.findAll();
    assertTrue(all.contains(board));
  }

  @Test
  public void findById() throws Exception {
    IBoard board = getNewBoard();
    toDelete(board);
    assertNotNull(board.getId());
    Optional<IBoard> byId = boardService.findById(board.getId());
    assertNotNull(byId.get());
  }

  @Test
  public void delete() throws Exception {
    IBoard board = getNewBoard();
    String boardId = board.getId();
    assertNotNull(boardId);
    boardService.delete(boardId);
    Optional<IBoard> byId = boardService.findById(boardId);
    assertTrue(!byId.isPresent());
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