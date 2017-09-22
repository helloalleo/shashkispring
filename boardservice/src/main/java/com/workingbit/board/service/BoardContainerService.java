package com.workingbit.board.service;

import com.workingbit.board.dao.BoardContainerDao;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.model.CreateBoardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Aleksey Popryaduhin on 07:00 22/09/2017.
 */
@Service
public class BoardContainerService {

  private final BoardContainerDao boardContainerDao;

  @Autowired
  public BoardContainerService(BoardContainerDao boardContainerDao) {
    this.boardContainerDao = boardContainerDao;
  }

  public BoardContainer createBoard(CreateBoardRequest createBoardRequest) {
    return null;
  }

  public Optional<BoardContainer> findById(String boardContainerId) {
    return null;
  }

  public void delete(String boardContainerId) {

  }

  public BoardContainer highlight(BoardContainer boardContainer) {
    return null;
  }

  public Optional<BoardContainer> move(BoardContainer boardContainer) {
    return null;
  }
}
