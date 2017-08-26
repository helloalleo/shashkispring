package com.workingbit.share.domain.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.workingbit.board.common.DBConstants;
import com.workingbit.share.domain.BaseDomain;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Aleksey Popryaduhin on 18:31 09/08/2017.
 */
@DynamoDBTable(tableName = DBConstants.ARTICLE_TABLE)
@Data
@NoArgsConstructor
public class Article implements BaseDomain {

  @DynamoDBAutoGeneratedKey
  @DynamoDBHashKey(attributeName = "id")
  private String id;

  @DynamoDBAttribute(attributeName = "username")
  private String author;

  @DynamoDBAttribute(attributeName = "title")
  private String title;

  @DynamoDBAttribute(attributeName = "content")
  private String content;

  @DynamoDBTypeConvertedJson(targetType = Set.class)
  @DynamoDBAttribute(attributeName = "boardIds")
  private Set<String> boardIds = new HashSet<>();

  /**
   * New article not viewed by moderator
   */
  @DynamoDBAttribute(attributeName = "newAdded")
  private boolean newAdded;

  /**
   * Article viewed by moderator and didn't pass it
   */
  @DynamoDBAttribute(attributeName = "banned")
  private boolean banned;

  /**
   * Article published
   */
  @DynamoDBAttribute(attributeName = "published")
  private boolean published;

  public Article(String author, String title, String content) {
    this.author = author;
    this.title = title;
    this.content = content;
  }
}