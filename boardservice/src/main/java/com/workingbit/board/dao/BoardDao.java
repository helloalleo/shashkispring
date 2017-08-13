package com.workingbit.board.dao;

import com.workingbit.board.config.AWSProperties;
import com.workingbit.share.dao.BaseDao;
import com.workingbit.share.domain.IBoard;
import com.workingbit.share.domain.impl.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 18:16 09/08/2017.
 */
@Component
public class BoardDao extends BaseDao<Board, IBoard> {

  @Autowired
  public BoardDao(AWSProperties awsProperties) {
    super(Board.class, IBoard.class, awsProperties.getRegion(), awsProperties.getEndpoint(), awsProperties.isTest());
  }
}
