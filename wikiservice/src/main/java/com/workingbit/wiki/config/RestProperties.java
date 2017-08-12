package com.workingbit.wiki.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 08:57 11/06/2017.
 */
@Component
public class RestProperties {

  @Value("${BOARD_API_URL}")
  private @Getter
  String boardApiUrl;

  @Value("${BOARD_RESOURCE}")
  private @Getter
  String boardResource;
}
