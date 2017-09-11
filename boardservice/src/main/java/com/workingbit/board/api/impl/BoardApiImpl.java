package com.workingbit.board.api.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.workingbit.board.api.BoardApi;
import com.workingbit.board.common.ResourceConstants;
import com.workingbit.board.exception.BoardServiceError;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.board.model.BeatenAndAllowedSquareMap;
import com.workingbit.board.model.CreateBoardRequest;
import com.workingbit.board.model.ResponseError;
import com.workingbit.board.service.BoardService;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.Square;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Aleksey Popryaduhin on 13:22 09/08/2017.
 */
@RestController
@RequestMapping(ResourceConstants.BOARD)
public class BoardApiImpl implements BoardApi {

  private final BoardService boardService;

  @Autowired
  public BoardApiImpl(BoardService boardService) {
    this.boardService = boardService;
  }

  @Override
  public ResponseEntity<List<BoardContainer>> listBoards(Integer limit) {
    PaginatedScanList<BoardContainer> boardContainers = boardService.findAll();
    return new ResponseEntity<>(boardContainers, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<BoardContainer> createBoard(CreateBoardRequest createBoardRequest) {
    BoardContainer board = boardService.createBoard(createBoardRequest);
    return new ResponseEntity<>(board, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<BoardContainer> findBoardById(String boardId) {
    Optional<BoardContainer> boardContainerOptional = boardService.findById(boardId);
    if (boardContainerOptional.isPresent()) {
      return new ResponseEntity<>(boardContainerOptional.get(), HttpStatus.OK);
    }
    throw new BoardServiceError("Board not found");
  }

  @Override
  public ResponseEntity<Void> deleteBoardById(String boardId) {
    boardService.delete(boardId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Override
  public ResponseEntity<List<BeatenAndAllowedSquareMap>> highlightSquare(String boardId, Square toHighlight) {
    try {
      BeatenAndAllowedSquareMap highlight = boardService.highlight(boardId, toHighlight);
    } catch (BoardServiceException e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  //  @PostMapping(ResourceConstants.HIGHLIGHT)
//  public Map<String, Object> highlight(@RequestBody Map<String, Object> highlightFor) {
//    try {
//      Map<String, Object> highlighted = boardService.highlight(highlightFor);
//      return new HashMap<String, Object>() {{
//        put(ok.name(), true);
//        put(data.name(), highlighted);
//      }};
//    } catch (BoardServiceException e) {
//      return getErrorResponse(e);
//    }
//  }
//
//  @PostMapping(ResourceConstants.MOVE)
//  public Map<String, Object> move(@RequestBody Map<String, Object> moveTo) {
//    try {
//      Map<String, Object> move = boardService.move(moveTo);
//      return new HashMap<String, Object>() {{
//        put(ok.name(), true);
//        put(data.name(), move);
//      }};
//    } catch (BoardServiceException e) {
//      return getErrorResponse(e);
//    }
//  }
//
//  @PostMapping(ResourceConstants.UNDO)
//  public Map<String, Object> undo(@RequestBody Map<String, Object> undoInfo) {
//    try {
//      String boardIdStr = (String) undoInfo.get(boardId.name());
//      Map<String, Object> undo = boardService.undo(boardIdStr);
//      return new HashMap<String, Object>() {{
//        put(ok.name(), true);
//        put(data.name(), undo);
//      }};
//    } catch (BoardServiceException e) {
//      return getErrorResponse(e);
//    }
//  }
}
