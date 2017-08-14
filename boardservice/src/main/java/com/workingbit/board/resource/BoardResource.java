package com.workingbit.board.resource;

import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.workingbit.board.common.ResourceConstants;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.board.service.BoardService;
import com.workingbit.share.domain.IBoard;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.NewBoardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    }}).orElseGet(() -> new HashMap<String, Object>() {{
      put(ok.name(), false);
      put(message.name(), "Board not found");
    }});
  }

  @PostMapping()
  public Map<String, Object> create(@RequestBody NewBoardRequest newBoardRequest) {
    IBoard board = boardService.createBoard(newBoardRequest);
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
      return new HashMap<String, Object>() {{
        put(ok.name(), false);
        put(message.name(), e.getMessage());
      }};
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
      return new HashMap<String, Object>() {{
        put(ok.name(), false);
        put(message.name(), e.getMessage());
      }};
    }
  }
}
