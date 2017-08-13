package com.workingbit.history.converter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.rutledgepaulv.prune.Tree;

/**
 * Created by Aleksey Popryaduhin on 21:45 13/08/2017.
 */
//@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public interface MixIn {
  @JsonIgnore
  Tree.Node getRootOfTree();
}
