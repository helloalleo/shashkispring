package com.workingbit.board.api.impl;

import com.workingbit.board.api.BoardsApi;
import com.workingbit.board.model.Boards;
import com.workingbit.board.model.Strings;
import com.workingbit.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Aleksey Popryaduhin on 19:05 19/09/2017.
 */
@RestController
public class BoardsApiImpl implements BoardsApi {

  private BoardService boardService;

  @Autowired
  public BoardsApiImpl(BoardService boardService) {
    this.boardService = boardService;
  }

  @Override
  public ResponseEntity<Boards> listBoardsByIds(@RequestBody Strings boardIds) {
    Boards boards = boardService.findByIds(boardIds);
    return new ResponseEntity<>(boards, HttpStatus.OK);
  }
}
