package com.workingbit.board.config;

import com.workingbit.coremodule.config.AppProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 08:57 11/06/2017.
 */
@Component
public class BoardProperties extends AppProperties {

  @Value("${TEST}")
  private boolean test;

  @Value("${AWS_ENDPOINT}")
  private String endpoint;

  public boolean isTest() {
    return test;
  }

  public String getEndpoint() {
    return endpoint;
  }
}
