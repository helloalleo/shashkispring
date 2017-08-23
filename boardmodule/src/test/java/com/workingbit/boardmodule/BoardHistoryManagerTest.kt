package com.workingbit.boardmodule

import com.fasterxml.jackson.databind.ObjectMapper
import com.workingbit.coremodule.domain.impl.Board
import com.workingbit.coremodule.domain.impl.BoardContainer
import com.workingbit.coremodule.domain.impl.BoardHistory
import com.workingbit.coremodule.domain.impl.TreeNode
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.IOException
import java.util.*

/**
 * Created by Aleksey Popryaduhin on 19:39 23/08/2017.
 */
internal class BoardHistoryManagerTest {

    private var historyManagerService: BoardHistoryManager? = null
    private val mapper = ObjectMapper()

    @Before
    fun setUp() {
        this.historyManagerService = BoardHistoryManager(BoardHistory(UUID.randomUUID().toString(), "", TreeNode(null), TreeNode(null)))
    }

    @Test
    @Throws(Exception::class)
    fun addChangeable() {
        val board = getBoard()
        val node = historyManagerService!!.addBoard(board.boardContainer)
        assertNotNull(node)
    }

    @Test
    @Throws(Exception::class)
    fun undo() {
        val board = getBoard()
        historyManagerService!!.addBoard(board.boardContainer)
        assertNotNull(board)
    }

    @Test
    @Throws(Exception::class)
    fun redo() {
        val board = getBoard()
        historyManagerService!!.addBoard(board.boardContainer)
        historyManagerService!!.addBoard(board.boardContainer)
        assertNotNull(board)
        val undo = historyManagerService!!.undo()
        assertTrue(undo != null)
        val redo = historyManagerService!!.redo()
        assertTrue(redo != null)
    }

    /**
     * 1
     * 2--
     * 4-
     * 5
     * 3-
     *
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun redo_branch_first() {
        historyManagerService!!.addBoard(getBoard("1").boardContainer)
        historyManagerService!!.addBoard(getBoard("2").boardContainer)
        historyManagerService!!.addBoard(getBoard("3").boardContainer)
        var undo = historyManagerService!!.undo()
        assertEquals(undo?.id, "2")
        val redo = historyManagerService!!.redo()
        assertEquals(redo?.id, "3")
        undo = historyManagerService!!.undo()
        assertEquals(undo?.id, "2")

        historyManagerService!!.addBoard(getBoard("4").boardContainer)
        historyManagerService!!.addBoard(getBoard("5").boardContainer)
        undo = historyManagerService!!.undo()
        assertEquals(undo?.id, "4")
        undo = historyManagerService!!.undo()
        assertEquals(undo?.id, "2")
        undo = historyManagerService!!.undo()
        assertEquals(undo?.id, "1")
        historyManagerService!!.undo()
        assertFalse(historyManagerService!!.undo() != null)
    }

    @Test
    @Throws(Exception::class)
    fun redo_branch_custom() {
        historyManagerService!!.addBoard(getBoard("1").boardContainer)
        historyManagerService!!.addBoard(getBoard("2").boardContainer)
        historyManagerService!!.addBoard(getBoard("3").boardContainer)

        var undo = historyManagerService!!.undo()
        assertEquals(undo?.id, "2")
        val branch4 = historyManagerService!!.addBoard(getBoard("4").boardContainer)
        historyManagerService!!.addBoard(getBoard("44").boardContainer)
        historyManagerService!!.undo()
        historyManagerService!!.undo()
        assertEquals(undo?.id, "2")
        historyManagerService!!.addBoard(getBoard("5").boardContainer)
        undo = historyManagerService!!.undo()
        assertEquals(undo?.id, "2")
        if (branch4 != null ) {
            undo = historyManagerService!!.redo(branch4)
        }
        assertEquals(undo?.id, "4")
        undo = historyManagerService!!.redo()
        assertEquals(undo?.id, "44")
        assertFalse(historyManagerService!!.redo() != null)
    }

    @Test
    @Throws(Exception::class)
    fun undo_redo_with_recreated_history_manager() {
        historyManagerService!!.addBoard(getBoard("1").boardContainer)
        historyManagerService!!.addBoard(getBoard("2").boardContainer)
        historyManagerService!!.addBoard(getBoard("3").boardContainer)

        var undo = historyManagerService!!.undo()
        assertEquals(undo?.id, "2")
        historyManagerService!!.addBoard(getBoard("4").boardContainer)
        historyManagerService!!.addBoard(getBoard("44").boardContainer)
        val boardHistory = historyManagerService!!.boardHistory
        historyManagerService!!.undo()
        historyManagerService!!.undo()
        assertEquals(undo?.id, "2")
        historyManagerService!!.addBoard(getBoard("5").boardContainer)
        undo = historyManagerService!!.undo()
        assertEquals(undo?.id, "2")

        // should start from 44
        val boardHistoryManager = BoardHistoryManager(boardHistory)
        undo = boardHistoryManager.redo()
        assertFalse(undo != null)
        undo = boardHistoryManager.undo()
        assertEquals(undo?.id, "4")
        undo = boardHistoryManager.undo()
        assertEquals(undo?.id, "2")
        undo = boardHistoryManager.undo()
        assertEquals(undo?.id, "1")
    }

    @Test
    @Throws(IOException::class)
    fun should_serialize_deserialize() {
        historyManagerService!!.addBoard(getBoard("1").boardContainer)
        val boardHistory = historyManagerService!!.boardHistory
        val s = mapper.writeValueAsString(boardHistory)
        assertFalse(s.isEmpty())
        val boardHistory1 = mapper.readValue<BoardHistory>(s, BoardHistory::class.java!!)
        assertNotNull(boardHistory1)
    }
//  @Test
//  public void should_return_history_json() {
//    historyManagerService.addBoard(getBoard("1").boardContainer);
//    historyManagerService.addBoard(getBoard("2").boardContainer);
//    historyManagerService.addBoard(getBoard("3").boardContainer);
//
//    BoardHistory history = historyManagerService.getHistoryByBoardId("");
//    assertTrue(history.getHistory().startsWith("["));
//    assertTrue(history.getHistory().length() > 10);
//  }

//  @Test
//  public void should_deserialize_from_json() {
//    historyManagerService.addBoard(getBoard("1").boardContainer);
//    historyManagerService.addBoard(getBoard("2").boardContainer);
//    historyManagerService.addBoard(getBoard("3").boardContainer);
//
//    BoardHistory history = historyManagerService.getHistoryByBoardId("");
//    BoardTreeNode boardTreeNodeOrig = historyManagerService.getBoardTreeNode();
//    BoardTreeNode boardTreeNode = historyManagerService.createFromJson(history.getHistory());
//    assertNotNull(boardTreeNode);
//    assertEquals(boardTreeNodeOrig, boardTreeNode);
//  }

//  @Test
//  public void should_deserialize_from_json_tree() {
//    historyManagerService.addBoard(getBoard("1").boardContainer);
//    historyManagerService.addBoard(getBoard("2").boardContainer);
//    historyManagerService.addBoard(getBoard("3").boardContainer);
//
//    historyManagerService.undo();
//    BoardTreeNode branch4 = historyManagerService.addBoard(getBoard("4").boardContainer);
//    historyManagerService.addBoard(getBoard("44").boardContainer);
//    historyManagerService.undo();
//    historyManagerService.undo();
//    historyManagerService.addBoard(getBoard("5").boardContainer);
//    historyManagerService.undo();
//    historyManagerService.redo(branch4);
//    historyManagerService.redo();
//
//    BoardHistory history = historyManagerService.getHistoryByBoardId("");
//    BoardTreeNode boardTreeNodeOrig = historyManagerService.getBoardTreeNode();
//    BoardTreeNode boardTreeNode = historyManagerService.createFromJson(history.getHistory());
//    assertNotNull(boardTreeNode);
//    assertEquals(boardTreeNodeOrig, boardTreeNode);
//  }

    internal fun getBoard(id: String): Board {
        val board = Board()
        val currentBoard = BoardContainer()
        currentBoard.id = id
        board.boardContainer=currentBoard
        return board
    }

    internal fun getBoard(): Board {
        return getBoard(UUID.randomUUID().toString())
    }
}