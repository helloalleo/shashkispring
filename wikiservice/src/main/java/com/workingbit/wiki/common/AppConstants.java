package com.workingbit.wiki.common;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 13:10 09/08/2017.
 */
@Component
@PropertySource("classpath:constants.properties")
public class AppConstants {

  @Value("${CLIENT_URL}")
  private @Getter
  String clientUrl;
}
