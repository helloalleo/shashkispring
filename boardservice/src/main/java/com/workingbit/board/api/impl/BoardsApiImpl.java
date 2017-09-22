package com.workingbit.board.api.impl;

import com.workingbit.board.api.BoardsApi;
import com.workingbit.board.model.BoardContainers;
import com.workingbit.board.model.Strings;
import com.workingbit.board.service.BoardContainerService;
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

  private BoardContainerService boardContainerService;

  @Autowired
  public BoardsApiImpl(BoardContainerService boardContainerService) {
    this.boardContainerService = boardContainerService;
  }

  @Override
  public ResponseEntity<BoardContainers> listBoardContainersByIds(@RequestBody Strings boardContainerIds) {
    BoardContainers boards = boardContainerService.findByIds(boardContainerIds);
    return new ResponseEntity<>(boards, HttpStatus.OK);
  }
}
