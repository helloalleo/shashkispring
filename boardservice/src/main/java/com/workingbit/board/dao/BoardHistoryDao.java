package com.workingbit.board.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.board.config.AWSProperties;
import com.workingbit.share.domain.IBoard;
import com.workingbit.share.domain.IBoardHistory;
import com.workingbit.share.domain.impl.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Aleksey Popryaduhin on 18:16 09/08/2017.
 */
@Component
public class BoardHistoryDao extends BaseDao<BoardHistory, IBoardHistory> {

  private final ObjectMapper objectMapper;

  @Autowired
  public BoardDao(AWSProperties awsProperties,
                  ObjectMapper objectMapper) {
    super(BoardHistory.class, IBoardHistory.class, awsProperties.getRegion());
    this.objectMapper = objectMapper;
  }

//  @Override
//  public Optional<IBoard> findById(String entityId) {
//    Optional<IBoard> boardOptional = super.findById(entityId);
//    return boardOptional.map(iBoard -> {
//      BoardChanger currentBoard = iBoard.getCurrentBoard();
//      if (currentBoard != null) {
//        currentBoard.mapBoard(objectMapper);
//      }
//      return iBoard;
//    });
//  }
//
//  @Override
//  public List<IBoard> findAll() {
//    return super.findAll()
//        .stream()
//        .peek(iBoard -> {
//          BoardChanger currentBoard = iBoard.getCurrentBoard();
//          if (currentBoard != null) {
//            currentBoard.mapBoard(objectMapper);
//          }
//        })
//        .collect(Collectors.toList());
//  }
}
