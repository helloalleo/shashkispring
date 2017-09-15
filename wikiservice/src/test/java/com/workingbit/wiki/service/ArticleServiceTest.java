package com.workingbit.wiki.service;

import com.workingbit.share.domain.impl.Article;
import com.workingbit.share.model.CreateBoardRequest;
import com.workingbit.share.model.EnumRules;
import com.workingbit.wiki.common.EnumResponse;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by Aleksey Popryaduhin on 13:53 09/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ArticleServiceTest {

  @Test
  public void delete() throws Exception {
    HashMap<String, Object> save = articleService.createArticleAndBoard(getArticleAndBoard(getArticle(), getCreateBoardRequest()));
    Article iArticle = (Article) save.get(EnumResponse.article.name());
    articleService.delete(iArticle.getId());
    Optional<Article> byId = articleService.findById(iArticle.getId());
    assertFalse(byId.isPresent());
  }

  @Test
  public void findById() throws Exception {
    HashMap<String, Object> save = articleService.createArticleAndBoard(getArticleAndBoard(getArticle(), getCreateBoardRequest()));
    Article iArticle = (Article) save.get(EnumResponse.article.name());
    toDelete(iArticle);
    Optional<Article> byId = articleService.findById(iArticle.getId());
    assertTrue(byId.isPresent());
  }

  @Test
  public void publishArticle() throws Exception {
    Article article = getArticle();
    article.setNewAdded(true);
    HashMap<String, Object> save = articleService.createArticleAndBoard(getArticleAndBoard(article, getCreateBoardRequest()));
    Article iArticle = (Article) save.get(EnumResponse.article.name());
    toDelete(iArticle);
    boolean publishArticle = articleService.publishArticle(iArticle);
    assertTrue(publishArticle);
    Optional<Article> byId = articleService.findById(iArticle.getId());
    assertTrue(byId.isPresent());
    assertTrue(byId.get().isPublished());
  }

  @Test
  public void findPublishedArticles() throws Exception {
    Article article = getArticle();
    article.setNewAdded(true);
    HashMap<String, Object> save = articleService.createArticleAndBoard(getArticleAndBoard(article, getCreateBoardRequest()));
    Article iArticle = (Article) save.get(EnumResponse.article.name());
    toDelete(iArticle);
    boolean publishArticle = articleService.publishArticle(iArticle);
    assertTrue(publishArticle);
    List<Article> publishedArticles = articleService.findPublishedArticles();
    Article published = publishedArticles.get(publishedArticles.indexOf(iArticle));
    assertTrue(published.isPublished());
  }

  @Autowired
  private ArticleService articleService;

  @After
  public void tearUp() {
    articles.forEach(article -> articleService.delete(article.getId()));
  }

  @Test
  public void save() throws Exception {
    Article article = getArticle();
    HashMap<String, Object> save = articleService.createArticleAndBoard(getArticleAndBoard(article, getCreateBoardRequest()));
    Article iArticle = (Article) save.get(EnumResponse.article.name());
    Optional<Article> articleOptional = articleService.findById(iArticle.getId());
    assertTrue(articleOptional.isPresent());
    toDelete(articleOptional.get());
    assertEquals(articleOptional.get().getTitle(), "test1");
    assertEquals(articleOptional.get().getContent(), "article1");
  }

  @Test
  public void findAll() throws Exception {
    Article article = getArticle();
    HashMap<String, Object> save = articleService.createArticleAndBoard(getArticleAndBoard(article, getCreateBoardRequest()));
    Article iArticle = (Article) save.get(EnumResponse.article.name());
    toDelete(iArticle);
    assertTrue(articleService.findAll().contains(iArticle));
  }

  private Map<String, Object> getArticleAndBoard(Article article, CreateBoardRequest newBoardRequest) {
    return new HashMap<String, Object>() {{
      put(EnumResponse.article.name(), article);
      put(EnumResponse.board.name(), getCreateBoardRequest());
    }};
  }

  private List<Article> articles = new ArrayList<>();

  private void toDelete(Article save) {
    articles.add(save);
  }

  private Article getArticle() {
    return new Article("alex", "test1", "article1");
  }

  private CreateBoardRequest getCreateBoardRequest() {
    CreateBoardRequest createBoardRequest = new CreateBoardRequest();
    createBoardRequest.setFillBoard(true);
    createBoardRequest.setBlack(false);
    createBoardRequest.setRules(EnumRules.RUSSIAN);
    return createBoardRequest;
  }
}