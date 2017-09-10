package com.workingbit.board.api.impl;

import com.workingbit.board.common.ResourceConstants;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.board.service.BoardService;
import com.workingbit.share.domain.impl.BoardContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.workingbit.board.common.EnumResponse.message;
import static com.workingbit.board.common.EnumResponse.ok;

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

//  @GetMapping()
//  public Map<String, Object> findAll() {
//    PaginatedScanList<BoardContainer> boards = boardService.findAll();
//    return new HashMap<String, Object>() {{
//      put(ok.name(), true);
//      put(data.name(), boards);
//    }};
//  }

  @GetMapping(path = "/{id}")
  public ResponseEntity findById(@PathVariable("id") String articleId) {
    Optional<BoardContainer> boardOptional = boardService.findById(articleId);
    return boardOptional
        .<ResponseEntity>map(boardContainer -> new ResponseEntity<>(boardContainer, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>("Board not found", HttpStatus.NOT_FOUND));
  }

//  @PostMapping()
//  public Map<String, Object> create(@RequestBody NewBoardRequest newBoardRequest) {
//    BoardContainer board = boardService.createBoard(newBoardRequest);
//    return new HashMap<String, Object>() {{
//      put(ok.name(), true);
//      put(data.name(), board);
//    }};
//  }

//  @DeleteMapping()
//  public Map<String, Object> delete(String boardId) {
//    boardService.delete(boardId);
//    return new HashMap<String, Object>() {{
//      put(ok.name(), true);
//    }};
//  }
//
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


  // TOD Move to shared
  private Map<String, Object> getErrorResponse(String msg) {
    return new HashMap<String, Object>() {{
      put(ok.name(), false);
      put(message.name(), msg);
    }};
  }

  private Map<String, Object> getErrorResponse(BoardServiceException e) {
    return getErrorResponse(e.getMessage());
  }
}
