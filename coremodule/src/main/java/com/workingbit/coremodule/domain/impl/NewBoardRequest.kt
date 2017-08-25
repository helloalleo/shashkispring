package com.workingbit.share.domain.impl;

import com.workingbit.coremodule.common.EnumRules

/**
 * Created by Aleksey Popryaduhin on 15:58 10/08/2017.
 * TODO(MOVE TO ACTUAL CLASS)
 */
data class NewBoardRequest(private val map: HashMap<String, Any?>) {

    val fillBoard: Boolean by map
    val black: Boolean by map
    val rules: EnumRules by map
    val squareSize: Int by map
}
