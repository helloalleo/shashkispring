package com.workingbit.article.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.workingbit.article.config.AppProperties;
import com.workingbit.share.dao.BaseDao;
import com.workingbit.share.domain.impl.Article;
import com.workingbit.share.model.EnumArticleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.workingbit.article.common.EnumArticleKeys.*;

/**
 * Created by Aleksey Popryaduhin on 18:16 09/08/2017.
 */
@Component
public class ArticleDao extends BaseDao<Article> {

  @Autowired
  public ArticleDao(AppProperties appProperties) {
    super(Article.class, appProperties.getRegion(), appProperties.getEndpoint(), appProperties.isTest());
  }

  @Override
  public void save(Article entity) {
    super.save(entity);
  }

  public void publishArticle(Article article) {
    article.setState(EnumArticleState.published);
    save(article);
  }

  public List<Article> findPublished() {
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
    Map<String, AttributeValue> eav = new HashMap<>();
    eav.put(":trueVal", new AttributeValue().withN("1"));
    eav.put(":falseVal", new AttributeValue().withN("0"));
    String filterExpression = Published.name() + " = :trueVal and "
        + NewAdded.name() + " = :falseVal and "
        + Banned.name() + " = :falseVal";
    scanExpression.withFilterExpression(filterExpression).withExpressionAttributeValues(eav);
    PaginatedScanList<Article> scanArticle = getDynamoDBMapper().scan(Article.class, scanExpression);
    scanArticle.loadAllResults();
    List<Article> articles = new ArrayList<>(scanArticle.size());
    articles.addAll(scanArticle);
    return articles;
  }
}
