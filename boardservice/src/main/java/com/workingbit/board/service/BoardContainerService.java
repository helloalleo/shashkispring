package com.workingbit.board.service;

import com.workingbit.board.dao.BoardContainerDao;
import com.workingbit.board.exception.BoardServiceException;
import com.workingbit.board.model.BoardContainers;
import com.workingbit.board.model.Strings;
import com.workingbit.share.common.Log;
import com.workingbit.share.domain.impl.Board;
import com.workingbit.share.domain.impl.BoardContainer;
import com.workingbit.share.model.CreateBoardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Aleksey Popryaduhin on 07:00 22/09/2017.
 */
@Service
public class BoardContainerService {

  private final BoardContainerDao boardContainerDao;
  private final BoardService boardService;

  @Autowired
  public BoardContainerService(BoardContainerDao boardContainerDao, BoardService boardService) {
    this.boardContainerDao = boardContainerDao;
    this.boardService = boardService;
  }

  public BoardContainer createBoard(CreateBoardRequest createBoardRequest) {
    Board board = boardService.createBoard(createBoardRequest);

    BoardContainer boardContainer = new BoardContainer(board);
    boardContainer.setArticleId(createBoardRequest.getArticleId());
    save(boardContainer);

    board.setBoardContainerId(boardContainer.getId());
    boardService.save(board);
    return boardContainer;
  }

  public Optional<BoardContainer> findById(String boardContainerId) {
    return boardContainerDao.findById(boardContainerId).map(this::updateBoardContainer);
  }

  private BoardContainer updateBoardContainer(BoardContainer boardContainer) {
    Optional<Board> boardOptional = boardService.findById(boardContainer.getCurrentBoardId());
    return boardOptional.map(board -> {
      Board updateBoard = BoardUtils.updateBoard(board);
      boardContainer.setCurrentBoard(updateBoard);
      return boardContainer;
    }).orElseGet(null);
  }

  public void delete(String boardContainerId) {
    boardContainerDao.findById(boardContainerId)
        .map(boardContainer -> {
          boardService.delete(boardContainer.getCurrentBoardId());
          boardContainerDao.delete(boardContainer.getId());
          return null;
        });
  }

  public Optional<BoardContainer> highlight(BoardContainer boardContainer) {
    return findById(boardContainer.getId())
        .map(updated -> {
          Board currentBoard = updated.getCurrentBoard();
          try {
            currentBoard = boardService.highlight(currentBoard);
            if (currentBoard == null) {
              return null;
            }
          } catch (BoardServiceException e) {
            Log.error(e.getMessage(), e);
            return null;
          }
          updated.setCurrentBoard(currentBoard);
          return updated;
        });
  }

  public Optional<BoardContainer> move(BoardContainer boardContainer) {
    return findById(boardContainer.getId())
        .map(updated -> {
          Board currentBoard = updated.getCurrentBoard();
          try {
            currentBoard = boardService.move(currentBoard);
            if (currentBoard == null) {
              return null;
            }
          } catch (BoardServiceException e) {
            Log.error(e.getMessage(), e);
            return null;
          }
          updated.setCurrentBoard(currentBoard);
          return updated;
        });
  }

  private void save(BoardContainer boardContainer) {
    boardContainerDao.save(boardContainer);
  }

  public BoardContainers findByIds(Strings boardIds) {
    List<String> ids = new ArrayList<>(boardIds.size());
    ids.addAll(boardIds);
    List<BoardContainer> boardContainerList = boardContainerDao.findByIds(ids)
        .stream()
        .map(this::updateBoardContainer)
        .collect(Collectors.toList());
    BoardContainers boardContainers = new BoardContainers();
    boardContainers.addAll(boardContainerList);
    return boardContainers;
  }
}
