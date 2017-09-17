package com.workingbit.article.api.impl;

import com.workingbit.article.api.ArticleApi;
import com.workingbit.article.service.ArticleService;
import com.workingbit.share.domain.impl.Article;
import com.workingbit.share.model.CreateArticleRequest;
import com.workingbit.share.model.CreateArticleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.workingbit.article.common.EnumResponse.*;

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

  @GetMapping()
  public Map<String, Object> findAll(Integer limit) {
    List<Article> articles = articleService.findAll(limit);
    return new HashMap<String, Object>() {{
      put(ok.name(), true);
      put(data.name(), articles);
    }};
  }

  @GetMapping(path = "/{id}")
  public Map<String, Object> findById(@PathVariable("id") String articleId) {
    Optional<Article> articleOptional = articleService.findById(articleId);
    return articleOptional.<Map<String, Object>>map(iArticle -> new HashMap<String, Object>() {{
        put(ok.name(), true);
        put(data.name(), iArticle);
      }}
    ).orElseGet(() -> new HashMap<String, Object>() {{
      put(ok.name(), false);
      put(message.name(), "Article not found");
    }});
  }

  @PostMapping()
  public ResponseEntity<CreateArticleResponse> create(@RequestBody CreateArticleRequest articleBody) {
    CreateArticleResponse articleAndBoard = articleService.createArticleAndBoard(articleBody);
    return new ResponseEntity<>(articleAndBoard, HttpStatus.OK);
  }

  @DeleteMapping()
  public Map<String, Object> delete(String articleId) {
    articleService.delete(articleId);
    return new HashMap<String, Object>() {{
      put(ok.name(), true);
    }};
  }

  @PutMapping(path = "/publish")
  public Map<String, Object> publishArticle(@RequestBody Article request) {
    boolean published = articleService.publishArticle(request);
    return new HashMap<String, Object>() {{
      put(ok.name(), true);
      put(data.name(), published);
    }};
  }

  @GetMapping(path = "/published")
  public Map<String, Object> findPublishedArticles() {
    List<Article> publishedArticles = articleService.findPublishedArticles();
    return new HashMap<String, Object>() {{
      put(ok.name(), true);
      put(data.name(), publishedArticles);
    }};
  }
}
