package com.workingbit.coremodule.domain.impl

/**
 * Created by Aleksey Popryaduhin on 16:58 23/08/2017.
 */
data class BoardContainer(val map: HashMap<String, Any?>) {

    val id: String by map

}