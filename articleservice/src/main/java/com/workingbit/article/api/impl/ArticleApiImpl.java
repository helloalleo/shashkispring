package com.workingbit.article.api.impl;

import com.workingbit.article.api.ArticleApi;
import com.workingbit.article.exception.ArticleServiceException;
import com.workingbit.article.service.ArticleService;
import com.workingbit.share.domain.impl.Article;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.model.CreateArticleRequest;
import com.workingbit.share.model.CreateArticleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Created by Aleksey Popryaduhin on 13:22 09/08/2017.
 */
@RestController
public class ArticleApiImpl implements ArticleApi {

  private final ArticleService articleService;

  @Autowired
  public ArticleApiImpl(ArticleService articleService) {
    this.articleService = articleService;
  }

  @Override
  public ResponseEntity<Article> findArticleById(@PathVariable String articleId) {
    Optional<Article> articleOptional = articleService.findById(articleId);
    return articleOptional
        .map(article -> new ResponseEntity<>(article, HttpStatus.OK))
        .orElseThrow(() -> new ArticleServiceException("Article not found"));
  }

  @Override
  public ResponseEntity<CreateArticleResponse> createArticle(@RequestBody CreateArticleRequest createArticleRequest) {
    CreateArticleResponse articleAndBoard = articleService.createArticleAndBoard(createArticleRequest);
    return new ResponseEntity<>(articleAndBoard, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> deleteArticleById(@PathVariable String articleId) {
    articleService.delete(articleId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Override
  public ResponseEntity<BoardContainer> findBoardByArticleId(@PathVariable String articleId) {
    Optional<BoardContainer> boardContainerOptional = articleService.findBoardByArticleId(articleId);
    return boardContainerOptional
        .map(boardContainer -> new ResponseEntity<>(boardContainer, HttpStatus.OK))
        .orElseThrow(() -> new ArticleServiceException("Board not found"));
  }

  //  @PutMapping(path = "/publish")
//  public Map<String, Object> publishArticle(@RequestBody Article request) {
//    boolean published = articleService.publishArticle(request);
//    return new HashMap<String, Object>() {{
//      put(ok.name(), true);
//      put(data.name(), published);
//    }};
//  }
//
//  @GetMapping(path = "/published")
//  public Map<String, Object> findPublishedArticles() {
//    List<Article> publishedArticles = articleService.findPublishedArticles();
//    return new HashMap<String, Object>() {{
//      put(ok.name(), true);
//      put(data.name(), publishedArticles);
//    }};
//  }
}
