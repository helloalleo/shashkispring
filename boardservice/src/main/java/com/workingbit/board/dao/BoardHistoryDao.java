package com.workingbit.board.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.workingbit.board.common.EnumBaseKeys;
import com.workingbit.board.config.AppProperties;
import com.workingbit.share.dao.BaseDao;
import com.workingbit.share.domain.impl.BoardHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.workingbit.share.common.Utils.isBlank;

/**
 * Created by Aleksey Popryaduhin on 14:51 14/08/2017.
 */
@Component
public class BoardHistoryDao extends BaseDao<BoardHistory> {

  @Autowired
  public BoardHistoryDao(AppProperties appProperties) {
    super(BoardHistory.class, appProperties.getRegion(), appProperties.getEndpoint(), appProperties.isTest());
  }

  public Optional<BoardHistory> findByBoardId(String boardId) {
    if (isBlank(boardId)) {
      return Optional.empty();
    }
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
    Map<String, AttributeValue> eav = new HashMap<>();
    eav.put(":boardId", new AttributeValue().withS(boardId));
    String filterExpression = EnumBaseKeys.boardId.name() + " = :boardId";
    scanExpression.withFilterExpression(filterExpression).withExpressionAttributeValues(eav);

    PaginatedScanList<BoardHistory> entity = getDynamoDBMapper().scan(BoardHistory.class, scanExpression);
    if (!entity.isEmpty()) {
      return Optional.of(entity.get(0));
    }
    return Optional.empty();
  }
}
