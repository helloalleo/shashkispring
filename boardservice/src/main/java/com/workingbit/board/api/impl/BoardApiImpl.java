package com.workingbit.board.api.impl;

import com.workingbit.board.api.BoardApi;
import com.workingbit.board.service.BoardContainerService;
import com.workingbit.share.domain.impl.BoardBox;
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

  private final BoardContainerService boardContainerService;

  @Autowired
  public BoardApiImpl(BoardContainerService boardContainerService) {
    this.boardContainerService = boardContainerService;
  }

  @Override
  public ResponseEntity<BoardBox> createBoard(@RequestBody CreateBoardRequest createBoardRequest) {
    BoardBox board = boardContainerService.createBoard(createBoardRequest);
    return new ResponseEntity<>(board, HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<BoardBox> findBoardById(@PathVariable String boardId) {
    Optional<BoardBox> boardContainerOptional = boardContainerService.findById(boardId);
    return boardContainerOptional.map(boardContainer -> new ResponseEntity<>(boardContainer, HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @Override
  public ResponseEntity<Void> deleteBoardById(@PathVariable String boardId) {
    boardContainerService.delete(boardId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Override
  public ResponseEntity<BoardBox> highlightBoard(@RequestBody BoardBox boardBox) {
    return boardContainerService.highlight(boardBox)
        .map(highlighted -> new ResponseEntity<>(highlighted, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @Override
  public ResponseEntity<BoardBox> move(@RequestBody BoardBox board) {
    return boardContainerService.move(board)
        .map(moved -> new ResponseEntity<>(moved, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
  }
}
