package com.workingbit.board.dao;

import com.workingbit.board.config.AWSProperties;
import com.workingbit.history.domain.impl.BoardHistory;
import com.workingbit.share.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 14:51 14/08/2017.
 */
@Component
public class BoardHistoryDao extends BaseDao<BoardHistory> {

  @Autowired
  public BoardHistoryDao(AWSProperties awsProperties) {
    super(BoardHistory.class, awsProperties.getRegion(), awsProperties.getEndpoint(), awsProperties.isTest());
  }
}
