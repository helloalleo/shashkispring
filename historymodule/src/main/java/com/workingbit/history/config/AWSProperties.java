package com.workingbit.history.config;

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
  private boolean test;

  public String getRegion() {
    return env.getProperty( "AWS_DEFAULT_REGION", System.getenv("AWS_DEFAULT_REGION"));
  }

  public boolean isTest() {
    return test;
  }

  public String getEndpoint() {
    return env.getProperty("ENDPOINT", "http://localhost:8000");
  }
}
