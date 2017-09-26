package com.workingbit.article.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 08:57 11/06/2017.
 */
@Component
public class AppProperties {

  private final Environment env;

  @Value("${TEST}")
  private
  boolean test;

  @Value("${AWS_ENDPOINT}")
  private
  String endpoint;

  @Value("${CLIENT_URL}")
  private String clientUrl;

  @Value("${CONTEXT_PATH}")
  private String contextPath;

  @Autowired
  public AppProperties(Environment env) {
    this.env = env;
  }

  public String getRegion() {
    return env.getProperty( "AWS_DEFAULT_REGION", System.getenv("AWS_DEFAULT_REGION"));
  }

  public boolean isTest() {
    return test;
  }

  public void setTest(boolean test) {
    this.test = test;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getClientUrl() {
    return clientUrl;
  }

  public String getContextPath() {
    return contextPath;
  }

  public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }
}
