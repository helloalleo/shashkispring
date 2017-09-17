package com.workingbit.article.api.impl;

import com.workingbit.article.api.ArticlesApi;
import com.workingbit.article.model.Articles;
import com.workingbit.article.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Aleksey Popryaduhin on 19:20 17/09/2017.
 */
@RestController
public class ArticlesApiImpl implements ArticlesApi {

  private final ArticleService articleService;

  @Autowired
  public ArticlesApiImpl(ArticleService articleService) {
    this.articleService = articleService;
  }

  @Override
  public ResponseEntity<Articles> listArticles(Integer limit) {
    Articles articles = articleService.findAll(limit);
    return new ResponseEntity<>(articles, HttpStatus.OK);
  }

}
