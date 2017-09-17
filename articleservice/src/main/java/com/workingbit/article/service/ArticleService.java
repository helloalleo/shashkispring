package com.workingbit.article.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.article.dao.ArticleDao;
import com.workingbit.share.domain.impl.Article;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.model.CreateArticleRequest;
import com.workingbit.share.model.CreateArticleResponse;
import com.workingbit.share.model.CreateBoardRequest;
import com.workingbit.share.model.EnumArticleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by Aleksey Popryaduhin on 13:45 09/08/2017.
 */
@Service
public class ArticleService {

  private final ArticleDao articleDao;
  private final BoardRemoteService boardRemoteService;
  private final ObjectMapper objectMapper;

  @Autowired
  public ArticleService(ArticleDao articleDao,
                        BoardRemoteService boardRemoteService,
                        ObjectMapper objectMapper) {
    this.articleDao = articleDao;
    this.boardRemoteService = boardRemoteService;
    this.objectMapper = objectMapper;
  }

  /**
   * Create an article an initialize it with a board
   *
   * @param articleAndBoard
   * @return
   */
  public CreateArticleResponse createArticleAndBoard(CreateArticleRequest articleAndBoard) {
    Article article = articleAndBoard.getArticle();
    CreateBoardRequest boardRequest = articleAndBoard.getBoardRequest();
    CreateArticleResponse createArticleResponse = new CreateArticleResponse();
    if (article.getBoardId().isEmpty()) {
      Optional<BoardContainer> boardOptional = boardRemoteService.createBoard(boardRequest);
      if (boardOptional.isPresent()) {
        article.setBoardId(boardOptional.get().getId());
        createArticleResponse.setArticle(article);
        createArticleResponse.setBoardContainer(boardOptional.get());
      }
    }
    article.setState(EnumArticleState.newcoming);
    articleDao.save(article);
    return createArticleResponse;
  }

  public List<Article> findAll(Integer limit) {
    return articleDao.findAll(limit);
  }

  public Optional<Article> findById(String articleId) {
    return articleDao.findById(articleId);
  }

  public void delete(String articleId) {
    articleDao.delete(articleId);
  }

  /**
   * Publish article
   *
   * @param article
   * @return was it published
   */
  public boolean publishArticle(Article article) {
    if (article.getState().equals(EnumArticleState.published)) {
      return false;
    }
    articleDao.publishArticle(article);
    return true;
  }

  /**
   * @return all published and not banned and not new articles
   */
  public List<Article> findPublishedArticles() {
    return articleDao.findPublished();
  }
}
