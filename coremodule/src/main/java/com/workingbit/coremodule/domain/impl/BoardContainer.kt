package com.workingbit.coremodule.domain.impl

import java.util.*

/**
 * Created by Aleksey Popryaduhin on 16:58 23/08/2017.
 */
data class BoardContainer(private val map: HashMap<String, Any?>) {

    constructor() : this(hashMapOf())

    var id: String? by map
    var squares: List<Square>  by map
    var whiteDraughts: ArrayList<Draught> by map
    var blackDraughts: ArrayList<Draught> by map
    var selectedSquare: Square by map
}