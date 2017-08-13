package com.workingbit.board.history;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.workingbit.board.common.DBConstants;
import com.workingbit.share.domain.IBoardHistory;
import com.workingbit.share.domain.impl.BoardHistoryNode;
import lombok.Data;

/**
 * Created by Aleksey Popryaduhin on 10:02 13/08/2017.
 */
@Data
@DynamoDBTable(tableName = DBConstants.BOARD_HISTORY_TABLE)
public class BoardHistory implements IBoardHistory {

  private String id;

  private BoardHistoryNode currentBoard;

  private BoardHistoryNode parentBoard;
}
