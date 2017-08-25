package com.workingbit.boardmodule.move

import com.workingbit.boardmodule.BoardUtils
import com.workingbit.boardmodule.BoardUtils.getDistanceVH
import com.workingbit.coremodule.domain.impl.BoardContainer
import com.workingbit.coremodule.domain.impl.Draught
import com.workingbit.coremodule.domain.impl.Square
import com.workingbit.coremodule.exception.BoardServiceException
import java.util.*

/**
 * Created by Aleksey Popryaduhin on 20:14 11/08/2017.
 */
class MoveManager @Throws(BoardServiceException::class)
internal constructor(val boardContainer: BoardContainer, sourceSquare: Square, targetSquare: Square, private val allowedMoves: List<Square>, private val beatenMoves: List<Draught>, private val undo: Boolean) {
    private val sourceSquare: Square
    private val targetSquare: Square

    init {
        /*
     */
        if (!undo && !allowedMoves.contains(targetSquare)) {
            throw BoardServiceException("Move not allowed")
        }
        this.sourceSquare = BoardUtils.findSquareLink(boardContainer, sourceSquare)!!
        this.targetSquare = BoardUtils.findSquareLink(boardContainer, targetSquare)!!
    }

    /**
     * Moves draught to new target and set board's selected square
     * @return Pair of updated board and date:
     * {moveDist: {v, h, queen}, targetSquare: Square} v - distance for moving vertical (minus up),
     * h - distance for move horizontal (minus left), targetSquare is a new square with
     * moved draught, queen is a draught has become the queen
     */
    fun moveAndUpdateBoard(): Pair<BoardContainer, Map<String, Any>> {
        return Pair<BoardContainer, Map<String, Any>>(boardContainer, moveDraught())
    }

    private fun moveDraught(): Map<String, Any> {
        targetSquare.draught = sourceSquare.draught
        targetSquare.draught?.v = targetSquare.v
        targetSquare.draught?.h=targetSquare.h
        targetSquare.draught?.highlighted = true
        sourceSquare.draught = null
        boardContainer.selectedSquare = targetSquare
        val distanceVH = getDistanceVH(sourceSquare, targetSquare)
        val vMove = distanceVH.first * sourceSquare.size
        val hMove = distanceVH.second * sourceSquare.size
        boardContainer.selectedSquare = targetSquare
        return object : HashMap<String, Any>() {
            init {
                put("moveDist", object : HashMap<String, Any>() {
                    init {
                        put("v", vMove)
                        put("h", hMove)
                        put("queen", targetSquare.draught?.queen!!)
                    }
                })
                put("undoMove", undo)
                put("selectedSquare", sourceSquare)
                put("targetSquare", targetSquare)
            }
        }
    }
}
