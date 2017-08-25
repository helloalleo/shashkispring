package com.workingbit.board.resource;

import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.workingbit.board.common.ResourceConstants;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.board.service.BoardService;
import com.workingbit.coremodule.domain.impl.Board;
import com.workingbit.share.domain.impl.NewBoardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.workingbit.board.common.EnumBaseKeys.boardId;
import static com.workingbit.board.common.EnumResponse.*;

/**
 * Created by Aleksey Popryaduhin on 13:22 09/08/2017.
 */
@RestController
@RequestMapping(ResourceConstants.BOARD)
public class BoardResource {

  private final BoardService boardService;

  @Autowired
  public BoardResource(BoardService boardService) {
    this.boardService = boardService;
  }

  @GetMapping()
  public Map<String, Object> findAll() {
    PaginatedScanList<Board> boards = boardService.findAll();
    return new HashMap<String, Object>() {{
      put(ok.name(), true);
      put(data.name(), boards);
    }};
  }

  @GetMapping(path = "/{id}")
  public Map<String, Object> findById(@PathVariable("id") String articleId) {
    Optional<Board> boardOptional = boardService.findById(articleId);
    return boardOptional.<Map<String, Object>>map(iBoard -> new HashMap<String, Object>() {{
      put(ok.name(), true);
      put(data.name(), iBoard);
    }}).orElseGet(() -> getErrorResponse("Board not found"));
  }

  @PostMapping()
  public Map<String, Object> create(@RequestBody NewBoardRequest newBoardRequest) {
    Board board = boardService.createBoard(newBoardRequest);
    return new HashMap<String, Object>() {{
      put(ok.name(), true);
      put(data.name(), board);
    }};
  }

  @DeleteMapping()
  public Map<String, Object> delete(String boardId) {
    boardService.delete(boardId);
    return new HashMap<String, Object>() {{
      put(ok.name(), true);
    }};
  }

  @PostMapping(ResourceConstants.HIGHLIGHT)
  public Map<String, Object> highlight(@RequestBody Map<String, Object> highlightFor) {
    try {
      Map<String, Object> highlighted = boardService.highlight(highlightFor);
      return new HashMap<String, Object>() {{
        put(ok.name(), true);
        put(data.name(), highlighted);
      }};
    } catch (BoardServiceException e) {
      return getErrorResponse(e);
    }
  }

  @PostMapping(ResourceConstants.MOVE)
  public Map<String, Object> move(@RequestBody Map<String, Object> moveTo) {
    try {
      Map<String, Object> move = boardService.move(moveTo);
      return new HashMap<String, Object>() {{
        put(ok.name(), true);
        put(data.name(), move);
      }};
    } catch (BoardServiceException e) {
      return getErrorResponse(e);
    }
  }

  @PostMapping(ResourceConstants.UNDO)
  public Map<String, Object> undo(@RequestBody Map<String, Object> undoInfo) {
    try {
      String boardIdStr = (String) undoInfo.get(boardId.name());
      Map<String, Object> undo = boardService.undo(boardIdStr);
      return new HashMap<String, Object>() {{
        put(ok.name(), true);
        put(data.name(), undo);
      }};
    } catch (BoardServiceException e) {
      return getErrorResponse(e);
    }
  }


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
