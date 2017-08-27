package com.workingbit.board.model;

import com.workingbit.share.domain.impl.Square;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Aleksey Popryaduhin on 09:23 27/08/2017.
 */
@Data
@AllArgsConstructor
public class MoveTracer {
  private Square allowed;
  private Square beaten;
  private Square previous;

  @Override
  public String toString() {
    return "MoveTracer{" +
        (allowed != null ? "allowed=" + allowed.toNotation() : "not allowed") +
        (beaten != null ? ", beaten=" + beaten.toNotation() : ", not beaten") +
        (previous != null ? ", previous=" + previous.toNotation() : ", no previous") +
        '}';
  }
}
