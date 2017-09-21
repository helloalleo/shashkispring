package com.workingbit.article.service;

import com.workingbit.article.config.RestProperties;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.model.CreateBoardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Created by Aleksey Popryaduhin on 07:31 12/08/2017.
 */
@Service
public class BoardRemoteService {

  private final RestTemplate restTemplate;
  private final RestProperties restProperties;

  @Autowired
  public BoardRemoteService(RestTemplate restTemplate,
                            RestProperties restProperties) {
    this.restTemplate = restTemplate;
    this.restProperties = restProperties;
  }

  public Optional<BoardContainer> createBoard(CreateBoardRequest createBoardRequest) throws URISyntaxException {
    ResponseEntity<BoardContainer> responseEntity = restTemplate.postForEntity(new URI(restProperties.getBoardResource()), createBoardRequest, BoardContainer.class);
    if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
      return Optional.of(responseEntity.getBody());
    }
    return Optional.empty();
  }

  public Optional<BoardContainer> findBoardById(String boardId) {
    BoardContainer boardContainer = restTemplate.getForObject(restProperties.getBoardResource() + "/" + boardId,
        BoardContainer.class);
    return Optional.ofNullable(boardContainer);
  }
}
