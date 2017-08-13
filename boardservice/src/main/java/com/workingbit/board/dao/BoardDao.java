package com.workingbit.board.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.board.config.AWSProperties;
import com.workingbit.share.dao.BaseDao;
import com.workingbit.share.domain.IBoard;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.BoardChanger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Aleksey Popryaduhin on 18:16 09/08/2017.
 */
@Component
public class BoardDao extends BaseDao<Board, IBoard> {

  private final ObjectMapper objectMapper;

  @Autowired
  public BoardDao(AWSProperties awsProperties,
                  ObjectMapper objectMapper) {
    super(Board.class, IBoard.class, awsProperties.getRegion());
    this.objectMapper = objectMapper;
  }
}
