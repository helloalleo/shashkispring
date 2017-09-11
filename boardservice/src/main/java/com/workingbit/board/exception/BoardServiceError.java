package com.workingbit.board.exception;

/**
 * Created by Aleksey Popryaduhin on 21:22 10/09/2017.
 */
public class BoardServiceError extends Error {
  public BoardServiceError(String msg) {
    super(msg);
  }
}
