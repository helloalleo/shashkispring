package com.workingbit.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.board.config.AppProperties;
import com.workingbit.board.dao.BoardDao;
import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.workingbit.board.service.BoardUtils.findSquareByVH;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Aleksey Popryaduhin on 21:15 11/08/2017.
 */
@SpringBootTest
public class BaseServiceTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  BoardService boardService;

  @Autowired
  BoardHistoryService boardHistoryService;

  @Autowired
  BoardDao boardDao;

  BoardContainer getBoard() {
    BoardContainer boardContainer = BoardUtils.initBoard(false, false, EnumRules.RUSSIAN, 60);
//    Board board = new Board(boardContainer, false, EnumRules.RUSSIAN, 60);
//    BoardContainer currentBoard = board.getCurrentBoard();
//    Optional<Square> squareByVH = BoardUtils.findSquareByVH(currentBoard, 5, 2);
//    Square selectedSquare = squareByVH.get();
//    Draught draught = new Draught(5, 2, getRules().getDimension());
//    selectedSquare.setDraught(draught);
//    currentBoard.setSelectedSquare(selectedSquare);
    return boardContainer;
  }

  Draught getDraught(int v, int h) {
    return new Draught(v, h, getRules().getDimension());
  }

  Square getSquare(Draught draught, int v, int h) {
    return new Square(v, h, getRules().getDimension(), true, 60, draught);
  }

  Draught getDraughtBlack(int v, int h) {
    return new Draught(v, h, getRules().getDimension(), true);
  }

  Square getSquareByVH(BoardContainer board, int v, int h) {
    return findSquareByVH(board, v, h).get();
  }

  protected EnumRules getRules() {
    return EnumRules.RUSSIAN;
  }

  BoardService getBoardServiceMock() {
    AppProperties appProperties = mock(AppProperties.class);
    when(appProperties.getRegion()).thenReturn("eu-central-1");
    BoardDao boardDao = new BoardDao(appProperties);
    return new BoardService(boardDao, objectMapper, boardHistoryService);
  }
}
