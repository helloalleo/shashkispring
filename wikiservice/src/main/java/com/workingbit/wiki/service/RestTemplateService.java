package com.workingbit.wiki.service;

import com.workingbit.wiki.config.RestProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

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

  public Map<String, Object> post(Object request) {
    return restTemplate.postForObject(boardResource(), request, Map.class);
  }
}
