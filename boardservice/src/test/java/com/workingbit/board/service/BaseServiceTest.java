package com.workingbit.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.board.config.AWSProperties;
import com.workingbit.board.dao.BoardDao;
import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.impl.*;
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

  Board getBoard() {
    assert boardService != null;
    Board board = boardService.createBoard(new NewBoardRequest(false, false, EnumRules.RUSSIAN, 60));
    return (Board) board;
  }

  Draught getDraught(int v, int h) {
    return new Draught(v, h);
  }

  Square getSquare(Draught draught, int v, int h) {
    return new Square(v, h, true, 60, draught);
  }

  Draught getDraughtBlack(int v, int h) {
    return new Draught(v, h, true);
  }

  Square getSquareByVH(BoardContainer board, int v, int h) {
    return findSquareByVH(board, v, h).get();
  }

  protected EnumRules getRules() {
    return EnumRules.RUSSIAN;
  }

  BoardService getBoardServiceMock() {
    AWSProperties awsProperties = mock(AWSProperties.class);
    when(awsProperties.getRegion()).thenReturn("eu-central-1");
    BoardDao boardDao = new BoardDao(awsProperties);
    return new BoardService(boardDao, objectMapper, boardHistoryService);
  }
}
