package com.workingbit.board.dao;

import com.workingbit.board.config.BoardProperties;
import com.workingbit.coremodule.dao.BaseDao;
import com.workingbit.coremodule.domain.impl.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 18:16 09/08/2017.
 */
@Component
public class BoardDao extends BaseDao<Board> {

  @Autowired
  public BoardDao(BoardProperties appProperties) {
    super(Board.class, appProperties.getRegion(), appProperties.getEndpoint(), appProperties.isTest());
  }
}
