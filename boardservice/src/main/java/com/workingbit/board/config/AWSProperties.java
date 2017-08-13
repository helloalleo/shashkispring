package com.workingbit.board.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 08:57 11/06/2017.
 */
@Component
@PropertySource("classpath:aws.properties")
public class AWSProperties {

  @Autowired
  private Environment env;

  public String getRegion() {
    return env.getProperty( "AWS_DEFAULT_REGION", System.getenv("AWS_DEFAULT_REGION"));
  }

  public boolean isTest() {
    return Boolean.parseBoolean(env.getProperty("TEST", "false"));
  }

  public String getEndpoint() {
    return env.getProperty("ENDPOINT", "http://localhost:8000");
  }
}
