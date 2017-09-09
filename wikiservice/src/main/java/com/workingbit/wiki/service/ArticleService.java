package com.workingbit.wiki.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.share.domain.impl.Article;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.domain.impl.NewBoardRequest;
import com.workingbit.wiki.common.EnumResponse;
import com.workingbit.wiki.dao.ArticleDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  public HashMap<String, Object> createArticleAndBoard(Map<String, Object> articleAndBoard) {
    Article article = objectMapper.convertValue(articleAndBoard.get(EnumResponse.article.name()), Article.class);
    NewBoardRequest newBoardRequest = objectMapper.convertValue(articleAndBoard.get(EnumResponse.board.name()), NewBoardRequest.class);
    final BoardContainer[] board = {null};
    if (article.getBoardIds().isEmpty()) {
      Optional<BoardContainer> boardOptional = boardRemoteService.createBoard(newBoardRequest);
      boardOptional.ifPresent(iBoard -> {
        article.getBoardIds().add(iBoard.getId());
        board[0] = iBoard;
      });
    }
    article.setNewAdded(true);
    articleDao.save(article);
    return new HashMap<String, Object>() {{
      put("article", article);
      put("board", board[0]);
    }};
  }

  /**
   * Add a new board into article
   *
   * @param article
   * @return
   */
  public void addBoard(Article article, String boardId) {
    article.getBoardIds().add(boardId);
    articleDao.save(article);
  }

  public List<Article> findAll() {
    return articleDao.findAll();
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
    if (article.isBanned()
        || article.isPublished()
        || !article.isNewAdded()
        || StringUtils.isBlank(article.getId())) {
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
