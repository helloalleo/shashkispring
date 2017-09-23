package com.workingbit.board.exception;

import com.workingbit.share.common.Log;

/**
 * Created by Aleksey Popryaduhin on 08:25 11/08/2017.
 */
public class BoardServiceException extends RuntimeException {

  public BoardServiceException(String message) {
    super(message);
    Log.error(message);
  }
}
