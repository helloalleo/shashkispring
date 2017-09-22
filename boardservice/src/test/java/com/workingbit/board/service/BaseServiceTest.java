package com.workingbit.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.board.dao.BoardDao;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.BoardBox;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;
import com.workingbit.share.model.CreateBoardRequest;
import com.workingbit.share.model.EnumRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.workingbit.board.service.BoardUtils.findSquareByVH;

/**
 * Created by Aleksey Popryaduhin on 21:15 11/08/2017.
 */
@SpringBootTest
public class BaseServiceTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  BoardBoxService boardBoxService;

//  @Autowired
//  BoardHistoryService boardHistoryService;

  @Autowired
  BoardDao boardDao;

  BoardBox getBoard() {
    Board board = BoardUtils.initBoard(false, false, EnumRules.RUSSIAN);
    BoardBox boardBox = new BoardBox(board);
//    Board board = new Board(boardBox, false, EnumRules.RUSSIAN, 60);
//    BoardBox currentBoard = board.getCurrentBoard();
//    Optional<Square> squareByVH = BoardUtils.findSquareByVH(currentBoard, 5, 2);
//    Square selectedSquare = squareByVH.get();
//    Draught draught = new Draught(5, 2, getRules().getDimension());
//    selectedSquare.setDraught(draught);
//    currentBoard.setSelectedSquare(selectedSquare);
    return boardBox;
  }

  Draught getDraught(int v, int h) {
    return new Draught(v, h, getRules().getDimension());
  }

  Square getSquare(Draught draught, int v, int h) {
    return new Square(v, h, getRules().getDimension(), true, draught);
  }

  Draught getDraughtBlack(int v, int h) {
    return new Draught(v, h, getRules().getDimension(), true);
  }

  Square getSquareByVH(BoardBox board, int v, int h) {
    return findSquareByVH(board.getCurrentBoard(), v, h).get();
  }

  protected EnumRules getRules() {
    return EnumRules.RUSSIAN;
  }

//  BoardService getBoardServiceMock() {
//    AppProperties appProperties = mock(AppProperties.class);
//    when(appProperties.getRegion()).thenReturn("eu-central-1");
//    BoardDao boardDao = new BoardDao(appProperties);
//    return new BoardService(boardDao, objectMapper, boardHistoryService);
//  }

  protected CreateBoardRequest getCreateBoardRequest() {
    CreateBoardRequest createBoardRequest = new CreateBoardRequest();
    createBoardRequest.setBlack(false);
    createBoardRequest.setFillBoard(false);
    createBoardRequest.setRules(EnumRules.RUSSIAN);
    return createBoardRequest;
  }
}
