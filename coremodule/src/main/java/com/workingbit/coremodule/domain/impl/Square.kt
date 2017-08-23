package com.workingbit.coremodule.domain.impl

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Created by Aleksey Popryaduhin on 17:03 23/08/2017.
 */
data class Square(private val map: HashMap<String, Any?>) {

    /**
     * row
     */
    private val v: Int by map
    /**
     * col
     */
    private val h: Int by map
    /**
     * on the main part where we have the draughts
     */
    private val main: Boolean by map
    /**
     * if square highlighted for allowing to move
     */
    private val highlighted: Boolean  by map
    private val size by map

    private val draught: Draught? by map

    /**
     * Selected draught is point for new recursion
     */
    private val pointDraught: Draught? by map
        @JsonIgnore get
}