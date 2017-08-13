package com.workingbit.history.domain.impl;

import com.workingbit.share.domain.impl.BoardContainer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class BoardHistoryNode {
  @Getter
  @Setter
  private BoardHistoryNode prev;
  @Getter
  @Setter
  private BoardContainer board;
  @Getter
  @Setter
  private BoardHistoryNode next;
}