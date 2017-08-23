package com.workingbit.share.domain.impl;

import com.workingbit.share.common.EnumRules;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Aleksey Popryaduhin on 15:58 10/08/2017.
 */
@Data
@AllArgsConstructor
public class NewBoardRequest implements Serializable {
  private boolean fillBoard = true;
  private boolean black;
  private EnumRules rules;
  private Integer squareSize;
}
