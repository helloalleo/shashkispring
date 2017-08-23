package com.workingbit.coremodule.domain.impl

/**
 * Created by Aleksey Popryaduhin on 16:59 23/08/2017.
 */
data class Draught(private val map: HashMap<String, Any?>) {

    /**
     * row
     */
    var v: Int by map
    /**
     * col
     */
    var h: Int by map

    var black: Boolean by map
    var queen: Boolean by map
    var beaten: Boolean by map
    var highlighted: Boolean by map
}