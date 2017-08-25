package com.workingbit.boardmodule.history

import com.fasterxml.jackson.databind.ObjectMapper
import com.workingbit.coremodule.domain.impl.BoardContainer
import com.workingbit.coremodule.domain.impl.BoardHistory
import com.workingbit.coremodule.domain.impl.TreeNode
import java.util.*

/**
 * Created by Aleksey Popryaduhin on 19:52 12/08/2017.
 */
class BoardHistoryManager {

    val boardHistory: BoardHistory
    private val mapper = ObjectMapper()
    private var current: TreeNode<BoardContainer>? = null

    //  public void setBoardTree(BoardHistory boardHistory) {
    //  }


    constructor(board: HashMap<String, Any?>) {
        boardHistory = BoardHistory(board)
    }

    constructor(boardHistory: BoardHistory) {
        this.boardHistory = boardHistory
        current = boardHistory.current
    }

//    fun getBoardHistory(): BoardHistory {
//        boardHistory.root = current?.getRootTree()
//        boardHistory.setCurrent(current)
//        return boardHistory
//    }

    /**
     * Adds a Changeable to manage.
     *
     * @param boardContainer
     */
    fun addBoard(boardContainer: BoardContainer): TreeNode<BoardContainer>? {
        val child = TreeNode(boardContainer)
        current?.addChild(child)
        current = child
        return current
    }

    private fun moveUp() {
        current = current?.parent
    }

    private fun moveDown(branch: TreeNode<BoardContainer>) {
        current = branch
    }

    private fun moveDown() {
        current = current?.children?.get(0)
    }

    private fun canUndo(): Boolean {
        return current?.parent?.data != null
    }

    private fun canRedo(): Boolean {
        return !(current?.children?.isEmpty() ?: false)
    }

    private fun canRedo(branch: TreeNode<BoardContainer>): Boolean {
        return current?.children?.contains(branch) ?: false
    }

    //  private BoardTreeNode getBoardTreeNodeFromJson(String json) {
    //    Log.debug("Read object from json " + json);
    //    try {
    //      return mapper.readValue(json, BoardTreeNode.class);
    //    } catch (IOException e) {
    //      return null;
    //    }
    //  }

    //  BoardTreeNode getBoardTreeNode() {
    //    return current.getRootOfTree();
    //  }

    //  private Tree<Tree.Node<BoardContainer>> getTree(Tree.Node<Optional<BoardContainer>> node) {
    //    return node.asTree()
    //        .mapAsNodes(optionalNode -> Tree.node(optionalNode.getData().orElse(null)))
    //        .deepClone(boardContainerNode -> boardContainerNode);
    //  }

//    fun serializeToJsonBoardTreeNode(): String {
//        try {
//            val boardTree = current
//            return mapper.writeValueAsString(boardTree)
//        } catch (e: JsonProcessingException) {
//            return ""
//        }
//
//    }

    /**
     * Undoes the Changeable at the current index.
     *
     * @throws IllegalStateException if canUndo returns false.
     */
    fun undo(): BoardContainer? {
        //validate
        if (!canUndo()) {
            return null
        }
        //set index
        moveUp()
        //undo
        val boardContainerOptional = current?.data
        boardContainerOptional.let { undo() }
        return boardContainerOptional
    }

    /**
     * Redoes the Changable at the current index.
     *
     * @throws IllegalStateException if canRedo returns false.
     */
    fun redo(branch: TreeNode<BoardContainer>): BoardContainer? {
        //validate
        if (!canRedo(branch)) {
            return null
        }
        //reset index
        moveDown(branch)
        //redo
        val boardContainer = current?.data
        boardContainer.let { redo() }
        return boardContainer
    }

    fun redo(): BoardContainer? {
        if (!canRedo()) {
            return null
        }
        moveDown()
        val boardContainer = current?.data
        boardContainer.let { redo() }
        return boardContainer
    }

    //  public BoardHistory getHistoryByBoardId(String id) {
    //    BoardHistory boardHistory = new BoardHistory();
    //    boardHistory.setHistory(history);
    //    boardHistory.setBoardId(id);
    //    return boardHistory;
    //  }

    //  public BoardTreeNode createFromJson(String json) {
    //    return getBoardTreeNodeFromJson(json);
    //  }
}