package com.workingbit.article.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 13:10 09/08/2017.
 */
@Component
@Profile("production")
@PropertySource("classpath:constants.properties")
public class AppConstants {

  @Value("${CLIENT_URL}")
  private String clientUrl;

  public String getClientUrl() {
    return clientUrl;
  }
}
