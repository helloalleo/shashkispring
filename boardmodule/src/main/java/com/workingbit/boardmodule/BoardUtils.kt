package com.workingbit.boardmodule

import com.fasterxml.jackson.databind.ObjectMapper
import com.workingbit.coremodule.domain.impl.BoardContainer
import com.workingbit.coremodule.domain.impl.Square
import org.apache.commons.lang3.tuple.Pair
import java.util.*

/**
 * Created by Aleksey Popryaduhin on 20:56 11/08/2017.
 */
internal object BoardUtils {

    /**
     * Find variable link to square from board
     * @param board
     * @param square
     * @return
     */
    fun findSquareLink(board: BoardContainer, square: Square): Square? {
        return findSquareByVH(board, square.v, square.h)
    }

    fun findSquareByVH(board: BoardContainer, v: Int, h: Int): Square? {
        for (square in board.squares) {
            if (square.h == h && square.v == v) {
                return square
            }
        }
        return null
    }

    /**
     * Get diff between source and target if h == -1 then we go left if h == 1 go right if v == -1 go up if v == 1 go up
     *
     * @param source
     * @param target
     * @return
     */
    fun getDistanceVH(source: Square, target: Square): Pair<Int, Int> {
        val vDist = target.v - source.v
        val hDist = target.h - source.h
        return Pair.of(vDist, hDist)
    }

    fun <T, I> mapList(squares: List<I>?, objectMapper: ObjectMapper, clazz: Class<T>, iclazz: Class<I>): List<I> {
        if (squares == null || squares.isEmpty()) {
            return emptyList()
        }
        val newSquares = ArrayList<I>(squares.size)
        // leave as is. Find by Id returns HashMap of getSquares() convert so we need to convert it to Square
        for (i in squares.indices) {
            val square = iclazz.cast(objectMapper.convertValue(squares[i], clazz))
            newSquares.add(square)
        }
        return newSquares
    }

    fun nextSquareByDir(board: BoardContainer, source: Square, dir: Map.Entry<Int, Int>): Square? {
        return findSquareByVH(board, source.v + dir.key, source.h + dir.value)
    }

}
