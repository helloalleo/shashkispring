package com.workingbit.boardmodule.move

import com.workingbit.boardmodule.BoardUtils
import com.workingbit.boardmodule.BoardUtils.getDistanceVH
import com.workingbit.boardmodule.dao.BoardDao
import com.workingbit.coremodule.common.EnumRules
import com.workingbit.coremodule.domain.impl.BoardContainer
import com.workingbit.coremodule.domain.impl.Draught
import com.workingbit.coremodule.domain.impl.Square
import com.workingbit.coremodule.exception.BoardServiceException
import uy.kohesive.injekt.injectLazy
import java.lang.Math.abs

/**
 * Created by Aleksey Popryaduhin on 19:39 10/08/2017.
 */
class HighlightMoveManager @Throws(BoardServiceException::class)
private constructor(private val board: BoardContainer, private val selectedSquare: Square?, private val rules: EnumRules) {

    private val boardDao: BoardDao by injectLazy()

    /**
     * possible directions of moving
     */
    private val dirs: Map<Int,Int>
    private val diagonal: HashMap<Pair<Int, Int>, (h: Int, v: Int, dim: Int) -> Int>

    init {
        if (selectedSquare == null) {
            throw BoardServiceException("Nothing selected")
        }
        selectedSquare.pointDraught = selectedSquare.draught
        selectedSquare.draught?.highlighted = true
        board.selectedSquare = selectedSquare

        this.diagonal = HashMap()
        this.diagonal.put(Pair(-1, -1), { h, v, dim -> mainDiagonal(h, v, dim) })
        this.diagonal.put(Pair(1, 1), { h, v, dim -> mainDiagonal(h, v, dim) })
        this.diagonal.put(Pair(1, -1), { h, v, dim -> subDiagonal(h, v, dim) })
        this.diagonal.put(Pair(-1, 1), { h, v, dim -> subDiagonal(h, v, dim) })

        this.dirs = mapOf(-1 to -1, 1 to 1, 1 to -1, -1 to 1)
    }

    @Throws(BoardServiceException::class)
    fun findAllowedMoves(): Map<String, Any> {
        return walk(selectedSquare!!, 0)
    }

    /**
     * @param highlightFor map of {boardId, selectedSquare}
     * @return map of {allowed, beaten}
     * @throws BoardServiceException
     */
    @Throws(BoardServiceException::class)
    fun highlight(highlightFor: Map<String, Any>): Map<String, Any>? {
        val aBoardId = highlightFor["boardId"] as String
        val board = boardDao.findById(aBoardId) ?: throw BoardServiceException("Board not found")
        return try {
            val square = highlightFor["selectedSquare"] as Square
            // remember selected square
            board.boardContainer.selectedSquare = square
            boardDao.save(board)
            // highlight moves for the selected square
            val highlightMoveUtil = HighlightMoveManager.getService(board.boardContainer, square, board.rules)
            highlightMoveUtil.findAllowedMoves()
        } catch (e: BoardServiceException) {
            null
        }

    }


    /**
     * Walk through desk
     *
     * @param selectedSquare square for which we do the algorithm
     * @param deep           how deep in recursion
     * @return {allow, beaten}
     */
    @Throws(BoardServiceException::class)
    private fun walk(selectedSquare: Square, deep: Int): Map<String, Any> {
        val allowedMoves = mutableListOf<Square>()
        val beatenMoves = mutableListOf<Draught>()
        val dimension = boardDimension
        val mainDiagonal = selectedSquare.v - selectedSquare.h
        val subDiagonal = dimension - selectedSquare.v - selectedSquare.h
        val squares = board.squares
        // split board's square list on rows
        val iterateRows = getRows(dimension, squares)
        val rowsIterator = iterateRows.iterator()
        while (rowsIterator.hasNext()) {
            // get next row
            val next = rowsIterator.next()
            for (currentSquare in next) {
                val distanceVH = BoardUtils.getDistanceVH(selectedSquare, currentSquare)
                // if selected draught is not queen and we near of it other words not far then 2 squares then go next else skip this square
                if (!currentSquare.main // if we on the main black square
                        || !selectedSquare.pointDraught?.queen!! && (abs(distanceVH.first) > 1 || abs(distanceVH.second) > 1)
                        || currentSquare.equals(selectedSquare)) {
                    continue
                }
                // if we on main diagonal or on sub diagonal of given square, so check square pos left and right
                if (isSquareOnMainDiagonal(mainDiagonal, currentSquare) || isSquareOnSubDiagonal(dimension, subDiagonal, currentSquare)) {
                    val forwardDirs = getForwardDirs(selectedSquare, currentSquare)
                    for (curDir in forwardDirs) {
                        val nextSquare = mustBeat(selectedSquare.pointDraught?.black!!, curDir, currentSquare, selectedSquare)
                        if (nextSquare != null) {
                            val walk = walk(nextSquare, deep)
                            allowedMoves.add(nextSquare)
                            beatenMoves.add(nextSquare.pointDraught!!)
                        } else if (canMove(selectedSquare, currentSquare)) {
                            allowedMoves.add(currentSquare)
                        }// if we can move forward by dir then add to allowed moves
                    }
                }
            }
        }
        val allowedAfterBeat = allowedMoves
                .filter { it.pointDraught != null }
        return object : HashMap<String, Any>() {
            init {
                val completeAllowed = if (allowedAfterBeat.isEmpty()) allowedMoves else allowedAfterBeat
                put("allowed", completeAllowed)
                put("beaten", beatenMoves)
            }
        }
    }

    private fun getForwardDirs(selectedSquare: Square, currentSquare: Square): Map<Int, Int> {
        val dirVH = getDirVH(selectedSquare, currentSquare)
        val forwardDirs = mutableMapOf<Int, Int>()
        if (currentSquare.occupied || selectedSquare.pointDraught?.queen!!) {
            val elements = dirs
                    .filter { it.key != dirVH.first * -1 && it.value != dirVH.second * -1 }
            forwardDirs.putAll(elements)
        } else {
            forwardDirs.put(dirVH.first , dirVH.second)
        }
        return forwardDirs
    }

    /**
     * Split board squares on chunks which means rows
     *
     * @param dimension length of chunks
     * @param squares   array to split
     * @return stream of list chunks
     */
    private fun getRows(dimension: Int, squares: List<Square>): Sequence<List<Square>> {
        return squares.asSequence().chunked(dimension)
    }

    private val boardDimension: Int
        get() = abs(rules.dimension)

    private fun isSquareOnSubDiagonal(dimension: Int, subDiagonal: Int, square: Square): Boolean {
        return dimension - square.v - square.h === subDiagonal
    }

    private fun isSquareOnMainDiagonal(mainDiagonal: Int, square: Square): Boolean {
        return square.v - square.h === mainDiagonal
    }

    private fun canMove(selectedSquare: Square, currentSquare: Square): Boolean {
        val black = selectedSquare.pointDraught?.black
        val queen = selectedSquare.pointDraught?.queen
        val beaten = selectedSquare.pointDraught?.beaten
        val dist = getDistanceVH(selectedSquare, currentSquare)
        return !queen!! &&
                // we have not beat yet
                !beaten!! &&
                // if current square is not occupied
                !currentSquare.occupied &&
                (
                        // rule for white draughts, they can go only up
                        !black!! && (dist.first == -1 && dist.second === -1 || dist.first === -1 && dist.second === 1) || // rule for black draughts, they can go only down
                                black && (dist.first === 1 && dist.second === -1 || dist.first === 1 && dist.second === 1))//        (
        // rules for queen
        //        selectedSquare.isOccupied()
        //            && selectedSquare.getDraught().isQueen()
        //            && selectedSquare != currentSquare
        //            || !selectedSquare.getDraught().isQueen()
        //            && (dir.first == -1 && dir.second == -1 || dir.first == 1 && dir.second == -1))
    }

    /**
     * Return allowed square for move after beat
     * @param black
     * @param dir
     * @param currentSquare
     * @param selectedSquare
     * @return
     */
    @Throws(BoardServiceException::class)
    private fun mustBeat(black: Boolean, dir: Map.Entry<Int, Int>, currentSquare: Square, selectedSquare: Square): Square? {
        val nextSquare = BoardUtils.nextSquareByDir(board, currentSquare, dir)
        if (nextSquare != null) {
            val currentDraught = currentSquare.draught
            val dimension = boardDimension
            val mainDiagonal = selectedSquare.v - selectedSquare.h
            val subDiagonal = dimension - selectedSquare.v - selectedSquare.h
            if (currentSquare.occupied
                    // current square is opposite color
                    && currentDraught?.black !== black
                    && (isSquareOnMainDiagonal(mainDiagonal, nextSquare) || isSquareOnSubDiagonal(dimension, subDiagonal, nextSquare))
                    // next square empty
                    && !nextSquare.occupied) {
                // store new point for recursion in next square
                currentDraught?.beaten = true
                nextSquare.pointDraught = currentDraught
                return nextSquare
            }
        }
        return null
    }

    private fun getDirVH(selectedSquare: Square, currentSquare: Square): Pair<Int, Int> {
        val distanceVH = getDistanceVH(selectedSquare, currentSquare)
        return Pair(distanceVH.first / abs(distanceVH.first),
                distanceVH.second / abs(distanceVH.second))
    }

    private fun mainDiagonal(h: Int, v: Int, dim: Int): Int = h - v

    private fun subDiagonal(h: Int, v: Int, dim: Int): Int = dim - h - v

    companion object {

        @Throws(BoardServiceException::class)
        fun getService(board: BoardContainer, selectedSquare: Square, rules: EnumRules): HighlightMoveManager {
            return HighlightMoveManager(board, selectedSquare, rules)
        }

        /**
         * метод возвращает поля на которые нужно стать, чтобы побить шашку
         *
         * //   * @param black
         * //   * @param sourceSquare
         * //   * @param dir
         * //   * @param deep
         * @returns {any}
         */
        //  private List<Square> walkFront(boolean black, Square sourceSquare, Pair<Integer, Integer> dir, int deep) {
        //    if (sourceSquare == null || deep >= 1) {
        //      return new ArrayList<>();
        //    }
        //    deep++;
        //    Map<Pair<Integer, Integer>, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> mustache = new HashMap<>();
        //    mustache.put(Pair.of(-1, -1), Pair.of(Pair.of(-1, 1), Pair.of(1, -1)));
        //    mustache.put(Pair.of(1, 1), Pair.of(Pair.of(-1, 1), Pair.of(1, -1)));
        //    mustache.put(Pair.of(1, -1), Pair.of(Pair.of(1, 1), Pair.of(-1, -1)));
        //    mustache.put(Pair.of(-1, 1), Pair.of(Pair.of(1, 1), Pair.of(-1, -1)));
        //    int h = sourceSquare.h;
        //    int v = sourceSquare.v;
        //    List<Square> allow = new ArrayList<>(), beat = new ArrayList<>();
        //    List<Square> items;
        //    for (Square currentSquare : this.board.getSquares()) {
        //      // go left
        //      Pair<Integer, Integer> left = mustache.get(dir).first;
        //      Map<String, Object> walkLeft = this.walk(currentSquare, deep);
        //      allow.addAll((Collection<? extends Square>) walkLeft.get(allowed.name()));
        //      items = (List<Square>) walkLeft.get(beaten.name());
        //      Map<String, Object> walkRight = this.walk(currentSquare, deep);
        //      allow.addAll((Collection<? extends Square>) walkRight.get(allowed.name()));
        //      items.addAll((Collection<? extends Square>) walkRight.get(beaten.name()));
        //      if (items.size() > 0) {
        //        Stream<Square> some = items.stream().filter(Objects::isNull);
        //        if (some != null) {
        //          int sourceDiff = diagonal.get(dir).apply(h, v, this.getBoardDimension());
        //          allow = allow
        //              .stream()
        //              .filter((s) -> diagonal.get(dir).apply(s.h, s.v, this.getBoardDimension()) == sourceDiff)
        //              .collect(Collectors.toList());
        //        } else {
        //          beat.add(currentSquare);
        //        }
        //      }
        //      v += dir.second;
        //      h += dir.first;
        //    }
        //    int sourceDiff = this.diagonal.get(dir).apply(sourceSquare.h, sourceSquare.v, this.getBoardDimension());
        //    if (beat.size() == 0) {
        //      return allow.stream().filter((s) -> this.diagonal.get(dir).apply(s.h, s.v, this.getBoardDimension()) == sourceDiff).collect(Collectors.toList());
        //    }
        //    return beat;
        //  }
    }
}
