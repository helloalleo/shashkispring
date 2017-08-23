package com.workingbit.coremodule.domain.impl

import java.util.*

/**
 * Created by Aleksey Popryaduhin on 16:57 23/08/2017.
 */
data class Board(private val map: HashMap<String, Any?>) {

    private val id: String? by map
    private val squares: List<Square>  by map
    private val whiteDraughts: ArrayList<Draught> by map
    private val blackDraughts: ArrayList<Draught> by map
    private val selectedSquare: Square by map
}