package com.workingbit.article.service;

import com.workingbit.article.config.RestProperties;
import com.workingbit.share.domain.impl.BoardBox;
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

  public Optional<BoardBox> createBoard(CreateBoardRequest createBoardRequest) throws URISyntaxException {
    ResponseEntity<BoardBox> responseEntity = restTemplate.postForEntity(new URI(restProperties.getBoardResource()), createBoardRequest, BoardBox.class);
    if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
      return Optional.of(responseEntity.getBody());
    }
    return Optional.empty();
  }

  public Optional<BoardBox> findBoardById(String boardId) {
    BoardBox boardBox = restTemplate.getForObject(restProperties.getBoardResource() + "/" + boardId,
        BoardBox.class);
    return Optional.ofNullable(boardBox);
  }
}
