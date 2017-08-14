package com.workingbit.wiki.resource;

import com.workingbit.share.domain.IArticle;
import com.workingbit.share.domain.impl.Article;
import com.workingbit.wiki.common.ResourceConstants;
import com.workingbit.wiki.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.workingbit.wiki.common.EnumResponse.*;

/**
 * Created by Aleksey Popryaduhin on 13:22 09/08/2017.
 */
@RestController
@RequestMapping(ResourceConstants.ARTICLE)
public class ArticleResource {

  private final ArticleService articleService;

  @Autowired
  public ArticleResource(ArticleService articleService) {
    this.articleService = articleService;
  }

  @GetMapping()
  public Map<String, Object> findAll() {
    List<Article> articles = articleService.findAll();
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
  public Map<String, Object> create(@RequestBody Map<String, Object> articleBody) {
    HashMap<String, Object> articleAndBoard = articleService.createArticleAndBoard(articleBody);
    return new HashMap<String, Object>() {{
      put(ok.name(), true);
      put(data.name(), articleAndBoard);
    }};
  }

  @PostMapping("/addBoard/{boardId}")
  public Map<String, Object> addBoard(@RequestBody Article request, @PathVariable("boardId") String boardId) {
    articleService.addBoard(request, boardId);
    return new HashMap<String, Object>() {{
      put(ok.name(), true);
    }};
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
    List<IArticle> publishedArticles = articleService.findPublishedArticles();
    return new HashMap<String, Object>() {{
      put(ok.name(), true);
      put(data.name(), publishedArticles);
    }};
  }
}
