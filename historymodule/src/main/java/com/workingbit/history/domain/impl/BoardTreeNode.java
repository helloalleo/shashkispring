package com.workingbit.history.domain.impl;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.workingbit.history.domain.IBoardTreeNode;
import com.workingbit.share.domain.impl.BoardContainer;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Aleksey Popryaduhin on 08:40 14/08/2017.
 */
@Data
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class BoardTreeNode implements IBoardTreeNode {
  private BoardContainer data;
  private BoardTreeNode parent;
  private List<BoardTreeNode> children = new ArrayList<>();

  @Override
  public String toString() {
    return "BoardTreeNode{" +
        "data=" + data +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
//    if (!super.equals(o)) return false;
    BoardTreeNode that = (BoardTreeNode) o;
    return Objects.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), data);
  }
}
