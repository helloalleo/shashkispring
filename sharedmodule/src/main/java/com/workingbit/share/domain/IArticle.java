package com.workingbit.share.domain;

import java.util.Set;

/**
 * Created by Aleksey Popryaduhin on 08:47 12/08/2017.
 */
public interface IArticle {

  String getId();

  String getAuthor();

  String getTitle();

  String getContent();

  Set<String> getBoardIds();

  boolean isNewAdded();

  void setNewAdded(boolean isNewArticle);

  boolean isBanned();

  void setBanned(boolean isBanned);

  boolean isPublished();

  void setPublished(boolean isPublished);
}
