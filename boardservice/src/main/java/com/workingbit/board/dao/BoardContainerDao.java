package com.workingbit.board.dao;

import com.workingbit.board.config.AppProperties;
import com.workingbit.share.dao.BaseDao;
import com.workingbit.share.domain.impl.BoardContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 06:55 22/09/2017.
 */
@Component
public class BoardContainerDao extends BaseDao<BoardContainer>{

  @Autowired
  protected BoardContainerDao(AppProperties appProperties) {
    super(BoardContainer.class, appProperties.getRegion(), appProperties.getEndpoint(), appProperties.isTest());
  }
}
