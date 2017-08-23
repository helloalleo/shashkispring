package com.workingbit.share.domain.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.*
import com.workingbit.board.common.DbConstants
import com.workingbit.coremodule.common.EnumArticleStatuses
import lombok.Data
import lombok.NoArgsConstructor

/**
 * Created by Aleksey Popryaduhin on 18:31 09/08/2017.
 */
@DynamoDBTable(tableName = DbConstants.ARTICLE_TABLE)
public class Article(map: HashMap<String, Any?>) {

    private val id: String by map
        @DynamoDBAutoGeneratedKey @DynamoDBHashKey(attributeName = DbConstants.ID) get

    private val authorId: String by map
        @DynamoDBAttribute(attributeName = "authorId") get

    private val title: String by map
        @DynamoDBAttribute(attributeName = "title") get

    private val content: String by map
        @DynamoDBAttribute(attributeName = "content") get

    private val boardId: String by map
        @DynamoDBAttribute(attributeName = "boardId") get

    private val status: EnumArticleStatuses by map
        @DynamoDBAttribute(attributeName = "status") @DynamoDBTypeConvertedEnum get
}