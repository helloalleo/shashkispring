package com.workingbit.article.service;

import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.article.config.RestProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Aleksey Popryaduhin on 07:26 12/08/2017.
 */
@Service
public class RestTemplateService extends BaseRestService {

  private final RestTemplate restTemplate;

  @Autowired
  public RestTemplateService(RestTemplate restTemplate,
                             RestProperties restProperties) {
    super(restProperties);
    this.restTemplate = restTemplate;
  }

  public BoardContainer post(Object request) {
    return restTemplate.postForObject(boardResource(), request, BoardContainer.class);
  }
}
