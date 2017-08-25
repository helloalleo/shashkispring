package com.workingbit.coremodule.domain.impl

/**
 * Created by Aleksey Popryaduhin on 16:58 23/08/2017.
 */
data class BoardContainer(private val map: HashMap<String, Any?>) {

    var id: String? by map
    var squares: List<Square>  by map
    var whiteDraughts: ArrayList<Draught> by map
    var blackDraughts: ArrayList<Draught> by map
    var selectedSquare: Square by map

    constructor(squares: ArrayList<Square>, whiteDraughts: ArrayList<Draught>, blackDraughts: ArrayList<Draught>, selectedSquare: Square?)
            : this(hashMapOf(
            "squares" to squares,
            "whiteDraughts" to whiteDraughts,
            "blackDraughts" to blackDraughts,
            "selectedSquare" to selectedSquare
    ))
}