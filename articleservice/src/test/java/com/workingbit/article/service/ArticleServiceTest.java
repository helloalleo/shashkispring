package com.workingbit.article.service;

import com.workingbit.share.domain.impl.Article;
import com.workingbit.share.model.*;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by Aleksey Popryaduhin on 13:53 09/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ArticleServiceTest {

  @Test
  public void delete() throws Exception {
    CreateArticleResponse save = articleService.createArticleAndBoard(getArticleAndBoard(getArticle(), getCreateBoardRequest()));
    Article iArticle = (Article) save.getArticle();
    articleService.delete(iArticle.getId());
    Optional<Article> byId = articleService.findById(iArticle.getId());
    assertFalse(byId.isPresent());
  }

  @Test
  public void findById() throws Exception {
    CreateArticleResponse save = articleService.createArticleAndBoard(getArticleAndBoard(getArticle(), getCreateBoardRequest()));
    Article iArticle = (Article) save.getArticle();
    toDelete(iArticle);
    Optional<Article> byId = articleService.findById(iArticle.getId());
    assertTrue(byId.isPresent());
  }

  @Test
  public void publishArticle() throws Exception {
    Article article = getArticle();
    article.setState(EnumArticleState.published);
    CreateArticleResponse save = articleService.createArticleAndBoard(getArticleAndBoard(article, getCreateBoardRequest()));
    Article iArticle = (Article) save.getArticle();
    toDelete(iArticle);
    // TODO
    boolean publishArticle = articleService.publishArticle(iArticle);
//    assertTrue(publishArticle);
    Optional<Article> byId = articleService.findById(iArticle.getId());
//    assertTrue(byId.isPresent());
//    assertTrue(byId.get().getState().equals(EnumArticleState.published));
  }

  @Test
  public void findPublishedArticles() throws Exception {
    Article article = getArticle();
    article.setState(EnumArticleState.published);
    CreateArticleResponse save = articleService.createArticleAndBoard(getArticleAndBoard(article, getCreateBoardRequest()));
    Article iArticle = (Article) save.getArticle();
    toDelete(iArticle);
    boolean publishArticle = articleService.publishArticle(iArticle);
    assertTrue(publishArticle);
    List<Article> publishedArticles = articleService.findPublishedArticles();
    Article published = publishedArticles.get(publishedArticles.indexOf(iArticle));
    assertTrue(published.getState().equals(EnumArticleState.published));
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
    CreateArticleResponse save = articleService.createArticleAndBoard(getArticleAndBoard(article, getCreateBoardRequest()));
    Article iArticle = (Article) save.getArticle();
    Optional<Article> articleOptional = articleService.findById(iArticle.getId());
    assertTrue(articleOptional.isPresent());
    toDelete(articleOptional.get());
    assertEquals(articleOptional.get().getTitle(), "test1");
    assertEquals(articleOptional.get().getContent(), "article1");
  }

  @Test
  public void findAll() throws Exception {
    Article article = getArticle();
    CreateArticleResponse save = articleService.createArticleAndBoard(getArticleAndBoard(article, getCreateBoardRequest()));
    Article iArticle = (Article) save.getArticle();
    toDelete(iArticle);
    // TODO
//    assertTrue(articleService.findAll(10).contains(iArticle));
  }

  private CreateArticleRequest getArticleAndBoard(Article article, CreateBoardRequest newBoardRequest) {
    CreateArticleRequest createArticleRequest = new CreateArticleRequest();
    createArticleRequest.setArticle(article);
    createArticleRequest.setBoardRequest(newBoardRequest);
    return createArticleRequest;
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