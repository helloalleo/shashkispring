package com.workingbit.board.api.impl;

import com.workingbit.board.api.BoardApi;
import com.workingbit.board.service.BoardBoxService;
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

  private final BoardBoxService boardBoxService;

  @Autowired
  public BoardApiImpl(BoardBoxService boardBoxService) {
    this.boardBoxService = boardBoxService;
  }

  @Override
  public ResponseEntity<BoardBox> createBoard(@RequestBody CreateBoardRequest createBoardRequest) {
    BoardBox board = boardBoxService.createBoard(createBoardRequest);
    return new ResponseEntity<>(board, HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<BoardBox> findBoardById(@PathVariable String boardId) {
    Optional<BoardBox> boardBoxOptional = boardBoxService.findById(boardId);
    return boardBoxOptional.map(boardBox -> new ResponseEntity<>(boardBox, HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @Override
  public ResponseEntity<Void> deleteBoardById(@PathVariable String boardId) {
    boardBoxService.delete(boardId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Override
  public ResponseEntity<BoardBox> highlightBoard(@RequestBody BoardBox boardBox) {
    return boardBoxService.highlight(boardBox)
        .map(highlighted -> new ResponseEntity<>(highlighted, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @Override
  public ResponseEntity<BoardBox> move(@RequestBody BoardBox board) {
    return boardBoxService.move(board)
        .map(moved -> new ResponseEntity<>(moved, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @Override
  public ResponseEntity<BoardBox> addDraught(@RequestBody BoardBox board) {
    return boardBoxService.addDraught(board)
        .map(moved -> new ResponseEntity<>(moved, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @Override
  public ResponseEntity<BoardBox> undo(@RequestBody BoardBox boardBox) {
    return boardBoxService.undo(boardBox)
        .map(undone -> new ResponseEntity<>(undone, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @Override
  public ResponseEntity<BoardBox> redo(@RequestBody BoardBox boardBox) {
    return boardBoxService.redo(boardBox)
        .map(redone -> new ResponseEntity<>(redone, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
  }
}
