package com.workingbit.coremodule.domain.impl

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Created by Aleksey Popryaduhin on 17:03 23/08/2017.
 */
data class Square(private val map: HashMap<String, Any?>) {

    /**
     * row
     */
    var v: Int by map
    /**
     * col
     */
    var h: Int by map
    /**
     * on the main part where we have the draughts
     */
    val main: Boolean by map
    /**
     * if square highlighted for allowing to move
     */
    var highlighted: Boolean  by map
    val size: Int by map

    var draught: Draught? by map

    /**
     * Selected draught is point for new recursion
     */
    var pointDraught: Draught? by map
        @JsonIgnore get

    val occupied: Boolean
        get() = draught != null

    constructor(v: Int, h: Int, size: Int?, draught: Draught?) : this(
            hashMapOf("v" to v,
                    "h" to h,
                    "main" to ((h + v + 1) % 2 == 0),
                    "size" to size,
                    "draught" to draught))
}