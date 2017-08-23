package com.workingbit.boardmodule

import com.workingbit.share.common.EnumRules
import com.workingbit.share.domain.impl.BoardContainer
import com.workingbit.share.domain.impl.Square

/**
 * Created by Aleksey Popryaduhin on 13:44 23/08/2017.
 */
public class HighlightMove(val boardContainer: BoardContainer, val selectedSquare: Square?, val rules: EnumRules) {
    init {
    }
}