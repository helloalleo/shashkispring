package com.workingbit.wiki.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.workingbit.share.dao.BaseDao;
import com.workingbit.share.domain.IArticle;
import com.workingbit.share.domain.impl.Article;
import com.workingbit.wiki.config.AWSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.workingbit.wiki.common.EnumArticleKeys.*;

/**
 * Created by Aleksey Popryaduhin on 18:16 09/08/2017.
 */
@Component
public class ArticleDao extends BaseDao<Article, IArticle> {

  private final AWSProperties awsProperties;

  @Autowired
  public ArticleDao(AWSProperties awsProperties) {
    super(Article.class, IArticle.class, awsProperties.getRegion());
    this.awsProperties = awsProperties;
  }

  @Override
  public void save(IArticle entity) {
    super.save(entity);
  }

  public void publishArticle(IArticle article) {
    article.setNewAdded(false);
    article.setBanned(false);
    article.setPublished(true);
    save(article);
  }

  public List<IArticle> findPublished() {
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
    List<IArticle> articles = new ArrayList<>(scanArticle.size());
    articles.addAll(scanArticle);
    return articles;
  }
}