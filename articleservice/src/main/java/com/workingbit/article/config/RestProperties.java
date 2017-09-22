package com.workingbit.article.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 08:57 11/06/2017.
 */
@Component
public class RestProperties {

  @Value("${BOARD_API_URL}")
  private
  String boardApiUrl;

  @Value("${BOARD_RESOURCE}")
  private
  String boardResource;

  public String getBoardApiUrl() {
    return boardApiUrl;
  }

  public void setBoardApiUrl(String boardApiUrl) {
    this.boardApiUrl = boardApiUrl;
  }

  public String getBoardResource() {
    return boardResource;
  }

  public void setBoardResource(String boardResource) {
    this.boardResource = boardResource;
  }
}
