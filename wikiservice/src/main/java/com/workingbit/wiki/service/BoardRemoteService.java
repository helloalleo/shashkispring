package com.workingbit.wiki.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.model.CreateBoardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.workingbit.wiki.common.EnumResponse.data;
import static com.workingbit.wiki.common.EnumResponse.ok;

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
    Map<String, Object> post = restTemplateService.post(createBoardRequest);
    if (((boolean) post.get(ok.name()))) {
      BoardContainer map = objectMapper.convertValue(post.get(data.name()), BoardContainer.class);
      return Optional.ofNullable(map);
    }
    return Optional.empty();
  }
}
