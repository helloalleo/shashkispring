package com.workingbit.article.config;

import lombok.Getter;
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
  private @Getter
  boolean test;

  @Value("${AWS_ENDPOINT}")
  private @Getter
  String endpoint;

  public String getRegion() {
    return env.getProperty( "AWS_DEFAULT_REGION", System.getenv("AWS_DEFAULT_REGION"));
  }
}
