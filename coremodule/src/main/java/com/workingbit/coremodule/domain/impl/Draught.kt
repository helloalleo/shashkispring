package com.workingbit.coremodule.domain.impl

/**
 * Created by Aleksey Popryaduhin on 16:59 23/08/2017.
 */
data class Draught(private val map: HashMap<String, Any?>) {

    /**
     * row
     */
    private val v: Int by map
    /**
     * col
     */
    private val h: Int by map

    private val black: Boolean by map
    private val queen: Boolean by map
    private val beaten: Boolean by map
    private val highlighted: Boolean by map
}