package com.workingbit.board.api.impl;

import com.workingbit.board.api.BoardsApi;
import com.workingbit.board.model.BoardBoxes;
import com.workingbit.board.model.Strings;
import com.workingbit.board.service.BoardBoxService;
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

  private BoardBoxService boardBoxService;

  @Autowired
  public BoardsApiImpl(BoardBoxService boardBoxService) {
    this.boardBoxService = boardBoxService;
  }

  @Override
  public ResponseEntity<BoardBoxes> listBoardBoxsByIds(@RequestBody Strings boardBoxIds) {
    BoardBoxes boards = boardBoxService.findByIds(boardBoxIds);
    return new ResponseEntity<>(boards, HttpStatus.OK);
  }
}
