package com.workingbit.article.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.article.dao.ArticleDao;
import com.workingbit.article.exception.ArticleServiceException;
import com.workingbit.article.model.Articles;
import com.workingbit.share.common.Utils;
import com.workingbit.share.domain.impl.Article;
import com.workingbit.share.domain.impl.BoardBox;
import com.workingbit.share.model.CreateArticleRequest;
import com.workingbit.share.model.CreateArticleResponse;
import com.workingbit.share.model.CreateBoardRequest;
import com.workingbit.share.model.EnumArticleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static com.workingbit.share.common.Utils.getRandomUUID;

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
    Utils.setRandomIdAndCreatedAt(article);
    article.setState(EnumArticleState.newadded);
    article.setBoardBoxId(getRandomUUID());
    CreateBoardRequest boardRequest = articleAndBoard.getBoardRequest();
    boardRequest.setBoardBoxId(article.getBoardBoxId());
    CreateArticleResponse createArticleResponse = new CreateArticleResponse();
    try {
      boardRequest.setArticleId(article.getId());
      Optional<BoardBox> boardBoxOptional = boardRemoteService.createBoard(boardRequest);
      if (boardBoxOptional.isPresent()) {
        article.setBoardBoxId(boardBoxOptional.get().getId());
        createArticleResponse.setArticle(article);
        createArticleResponse.setBoard(boardBoxOptional.get());
      } else {
        throw new ArticleServiceException("Unable to create board");
      }
    } catch (URISyntaxException e) {
      throw new ArticleServiceException("Invalid URI");
    }
    articleDao.save(article);
    return createArticleResponse;
  }

  public Articles findAll(Integer limit) {
    List<Article> articleList = articleDao.findAll(limit);
    Articles articles = new Articles();
    articles.addAll(articleList);
    return articles;
  }

  public Optional<Article> findById(String articleId) {
    return articleDao.findByKey(articleId);
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

  public Optional<BoardBox> findBoardByArticleId(String articleId) {
    Optional<Article> articleOptional = findById(articleId);
    return articleOptional.map(article -> {
      Optional<BoardBox> boardBox = boardRemoteService.findBoardById(article.getBoardBoxId());
      return boardBox.orElse(null);
    });
  }
}
