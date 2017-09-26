package com.workingbit.board.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 08:57 11/06/2017.
 */
@Component
public class AppProperties {

  @Autowired
  private Environment env;

  @Value("${TEST}")
  private
  boolean test;

  @Value("${AWS_ENDPOINT}")
  private
  String endpoint;

  @Value("${CLIENT_URL}")
  private String clientUrls;

  @Value("${CONTEXT_PATH}")
  private String contextPath;
  @Value("${HEADERS}")
  private String headers;
  @Value("${METHODS}")
  private String methods;

  public String getRegion() {
    return env.getProperty( "AWS_DEFAULT_REGION", System.getenv("AWS_DEFAULT_REGION"));
  }

  public boolean isTest() {
    return test;
  }

  public AppProperties setTest(boolean test) {
    this.test = test;
    return this;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getClientUrls() {
    return clientUrls;
  }

  public String getContextPath() {
    return contextPath;
  }

  public String getHeaders() {
    return headers;
  }

  public String getMethods() {
    return methods;
  }
}
