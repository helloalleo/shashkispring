package com.workingbit.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.board.config.AWSProperties;
import com.workingbit.board.dao.BoardDao;
import com.workingbit.board.history.BoardChangeManagerService;
import com.workingbit.share.common.EnumRules;
import com.workingbit.share.domain.IBoard;
import com.workingbit.share.domain.ISquare;
import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.NewBoardRequest;
import com.workingbit.share.domain.impl.Square;

import static com.workingbit.board.service.BoardUtils.findSquareByVH;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Aleksey Popryaduhin on 21:15 11/08/2017.
 */
class BaseServiceTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  IBoard getBoard() {
    BoardService boardService = getBoardService();
    return boardService.createBoard(new NewBoardRequest(false,false, EnumRules.RUSSIAN, 60));
  }

  BoardService getBoardService() {
    AWSProperties awsProperties = mock(AWSProperties.class);
    when(awsProperties.getRegion()).thenReturn("eu-central-1");
    when(awsProperties.isTest()).thenReturn(true);
    BoardDao boardDao = new BoardDao(awsProperties, objectMapper);
    return new BoardService(boardDao, getChangeManagerService(), objectMapper);
  }

  private BoardChangeManagerService getChangeManagerService() {
    return new BoardChangeManagerService();
  }

  Draught getDraught(int v, int h) {
    return new Draught(v, h);
  }

  ISquare getSquare(Draught draught, int v, int h) {
    return new Square(v, h, true, 60, draught);
  }

  Draught getDraughtBlack(int v, int h) {
    return new Draught(v, h, true);
  }

  ISquare getSquareByVH(IBoard board, int v, int h) {
    return findSquareByVH(board, v, h).get();
  }
}
