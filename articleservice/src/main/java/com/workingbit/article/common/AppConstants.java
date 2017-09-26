package com.workingbit.article.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 13:10 09/08/2017.
 */
@Component
public class AppConstants {

  @Value("${CLIENT_URL}")
  private String clientUrl;

  public static final String INTERNAL_SERVER_ERROR = "500";

  public String getClientUrl() {
    return clientUrl;
  }
}
