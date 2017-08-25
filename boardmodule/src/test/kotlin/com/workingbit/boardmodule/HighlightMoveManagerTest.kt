package com.workingbit.boardmodule

import com.workingbit.boardmodule.config.BoardProperties
import com.workingbit.boardmodule.dao.BoardDao
import com.workingbit.boardmodule.move.HighlightMoveManager
import com.workingbit.coremodule.common.EnumRules
import com.workingbit.coremodule.domain.ICoordinates
import com.workingbit.coremodule.domain.impl.Draught
import com.workingbit.coremodule.domain.impl.Square
import com.workingbit.coremodule.exception.BoardServiceException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.stream.Collectors

/**
 * Created by Aleksey Popryaduhin on 20:01 10/08/2017.
 */
class HighlightMoveUtilTest {

    private val squareSize = 60
    private fun getBoard() = BoardUtils.initBoard(true, false, EnumRules.RUSSIAN, squareSize)
    private fun getDraught(v: Int, h: Int) = Draught(5, 2)
    private fun getSquare(draught: Draught, v: Int, h: Int) = Square(v, h, squareSize, draught)
    private fun getRules() = EnumRules.RUSSIAN

    companion object {
        lateinit var dao: BoardDao

        @JvmStatic
        @BeforeEach
        fun setupTests() {
            dao = BoardDao(BoardProperties())
        }
    }

    @Test
    @Throws(Exception::class, BoardServiceException::class)
    fun `White draught should move on one square`() {
        val board = getBoard()
        val draught = getDraught(5, 2)
        val square = getSquare(draught, 5, 2)
        val highlightMoveUtil = HighlightMoveManager.getService(board.boardContainer, square, getRules())
        val allowedMoves = highlightMoveUtil.findAllowedMoves()
        assertTrue(allowedMoves.size > 0)
        assertEquals("(4,1)(4,3)", resultToString(allowedMoves, "allowed"))
    }

//    @Test
//    @Throws(Exception::class, BoardServiceException::class)
//    fun shouldBlackDraughtMoveBackwardOnOnePosition() {
//        val board = getBoard()
//        val draught = getDraughtBlack(5, 2)
//        val square = getSquare(draught, 5, 2)
//        val highlightMoveUtil = HighlightMoveUtil(board.getCurrentBoard(), square, getRules())
//        val allowedMoves = highlightMoveUtil.findAllowedMoves()
//        assertTrue(allowedMoves.size > 0)
//        assertEquals("(6,1)(6,3)", resultToString(allowedMoves, allowed))
//    }
//
//    @Test
//    @Throws(Exception::class, BoardServiceException::class)
//    fun shouldWhiteDraughtBeatForward() {
//        val board = getBoard()
//        val boardService = boardService
//        // add black draught
//        boardService.addDraught(board.getCurrentBoard(), getDraughtBlack(4, 3))
//        val draught = getDraught(5, 2)
//        // find square on board
//        val square = getSquareByVH(board.getCurrentBoard(), 5, 2)
//        // set draught for square
//        square.setDraught(draught)
//        val highlightMoveUtil = HighlightMoveUtil(board.getCurrentBoard(), square, getRules())
//        val allowedMoves = highlightMoveUtil.findAllowedMoves()
//        assertTrue(allowedMoves.size > 0)
//        assertEquals("(3,4)", resultToString(allowedMoves, allowed))
//        assertEquals("(4,3)", resultToString(allowedMoves, beaten))
//    }
//
//    @Test
//    @Throws(Exception::class, BoardServiceException::class)
//    fun shouldWhiteDraughtBeatForwardTwice() {
//        val board = getBoard()
//        val boardService = boardService
//        boardService.addDraught(board.getCurrentBoard(), getDraughtBlack(4, 3))
//        boardService.addDraught(board.getCurrentBoard(), getDraughtBlack(4, 1))
//        val draught = getDraught(5, 2)
//        val square = getSquareByVH(board.getCurrentBoard(), 5, 2)
//        square.setDraught(draught)
//        val highlightMoveUtil = HighlightMoveUtil(board.getCurrentBoard(), square, getRules())
//        val allowedMoves = highlightMoveUtil.findAllowedMoves()
//        assertTrue(allowedMoves.size > 0)
//        assertEquals("(3,0)(3,4)", resultToString(allowedMoves, allowed))
//        assertEquals("(4,1)(4,3)", resultToString(allowedMoves, beaten))
//    }
//
//    @Test
//    @Throws(Exception::class, BoardServiceException::class)
//    fun shouldWhiteDraughtBeatBackward() {
//        val board = getBoard()
//        val boardService = boardService
//        boardService.addDraught(board.getCurrentBoard(), getDraughtBlack(6, 1))
//        val draught = getDraught(5, 2)
//        val square = getSquareByVH(board.getCurrentBoard(), 5, 2)
//        square.setDraught(draught)
//        val highlightMoveUtil = HighlightMoveUtil(board.getCurrentBoard(), square, getRules())
//        val allowedMoves = highlightMoveUtil.findAllowedMoves()
//        assertTrue(allowedMoves.size > 0)
//        assertEquals("(7,0)", resultToString(allowedMoves, allowed))
//        assertEquals("(6,1)", resultToString(allowedMoves, beaten))
//    }
//
//    @Test
//    @Throws(Exception::class, BoardServiceException::class)
//    fun shouldWhiteDraughtBeatBackwardTwice() {
//        val board = getBoard()
//        val boardService = boardService
//        boardService.addDraught(board.getCurrentBoard(), getDraughtBlack(6, 1))
//        boardService.addDraught(board.getCurrentBoard(), getDraughtBlack(6, 3))
//        val draught = getDraught(5, 2)
//        val square = getSquareByVH(board.getCurrentBoard(), 5, 2)
//        square.setDraught(draught)
//        val highlightMoveUtil = HighlightMoveUtil(board.getCurrentBoard(), square, getRules())
//        val allowedMoves = highlightMoveUtil.findAllowedMoves()
//        assertTrue(allowedMoves.size > 0)
//        assertEquals("(7,0)(7,4)", resultToString(allowedMoves, allowed))
//        assertEquals("(6,1)(6,3)", resultToString(allowedMoves, beaten))
//    }

    private fun resultToString(allowedMoves: Map<String, Any>, search: String): String {
        return (allowedMoves[search] as List<ICoordinates>)
                .stream()
                .map { s -> String.format("(%s,%s)", s.getV(), s.getH()) }
                .collect(Collectors.joining())
    }
}