package com.workingbit.board.api.impl;

import com.workingbit.board.api.BoardApi;
import com.workingbit.board.exception.BoardServiceError;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.board.service.BoardService;
import com.workingbit.board.service.BoardUtils;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.model.CreateBoardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Created by Aleksey Popryaduhin on 13:22 09/08/2017.
 */
@RestController
public class BoardApiImpl implements BoardApi {

  private final BoardService boardService;

  @Autowired
  public BoardApiImpl(BoardService boardService) {
    this.boardService = boardService;
  }

  @Override
  public ResponseEntity<BoardContainer> createBoard(@RequestBody CreateBoardRequest createBoardRequest) {
    BoardContainer board = boardService.createBoard(createBoardRequest);
    return new ResponseEntity<>(board, HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<BoardContainer> findBoardById(@PathVariable String boardId) {
    Optional<BoardContainer> boardContainerOptional = boardService.findById(boardId);
    if (boardContainerOptional.isPresent()) {
      try {
        BoardContainer boardContainer = boardContainerOptional.get();
        BoardUtils.addDraught(boardContainer, "d4", false, true);
        return new ResponseEntity<>(boardContainer, HttpStatus.OK);
      } catch (BoardServiceException ignore) {
      }
    }
    throw new BoardServiceError("Board not found");
  }

  @Override
  public ResponseEntity<Void> deleteBoardById(@PathVariable String boardId) {
    boardService.delete(boardId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Override
  public ResponseEntity<BoardContainer> highlightBoard(@RequestBody BoardContainer boardContainer) {
    try {
      BoardContainer highlighted = boardService.highlight(boardContainer);
      return new ResponseEntity<>(highlighted, HttpStatus.OK);
    } catch (BoardServiceException e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public ResponseEntity<BoardContainer> move(@RequestBody BoardContainer board) {
    try {
      Optional<BoardContainer> move = boardService.move(board);
      return move.map(moved -> new ResponseEntity<>(moved, HttpStatus.OK))
          .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    } catch (BoardServiceException e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

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
