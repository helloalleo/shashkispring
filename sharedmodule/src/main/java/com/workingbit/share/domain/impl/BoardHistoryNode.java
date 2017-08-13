package com.workingbit.share.domain.impl;

import lombok.Data;

@Data
public class BoardHistoryNode {
  private BoardHistoryNode left;
  private BoardHistoryNode right;
  private BoardContainer board;
}