package com.workingbit.article.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.model.CreateBoardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Aleksey Popryaduhin on 07:31 12/08/2017.
 */
@Service
public class BoardRemoteService {

  private final RestTemplateService restTemplateService;
  private final ObjectMapper objectMapper;

  @Autowired
  public BoardRemoteService(RestTemplateService restTemplateService,
                            ObjectMapper objectMapper) {
    this.restTemplateService = restTemplateService;
    this.objectMapper = objectMapper;
  }

  public Optional<BoardContainer> createBoard(CreateBoardRequest createBoardRequest) {
    BoardContainer boardContainer = restTemplateService.post(restTemplateService.boardResource(), createBoardRequest);
    return Optional.ofNullable(boardContainer);
  }

  public Optional<BoardContainer> findBoardById(String boardId) {
    BoardContainer boardContainer = restTemplateService.get(restTemplateService.boardResource() + "/" + boardId);
    return Optional.ofNullable(boardContainer);
  }
}
