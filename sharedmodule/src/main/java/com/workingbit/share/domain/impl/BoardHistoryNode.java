package com.workingbit.share.domain.impl;

import com.workingbit.share.domain.IBoardContainer;
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
  private IBoardContainer board;
  @Getter
  @Setter
  private BoardHistoryNode next;
}