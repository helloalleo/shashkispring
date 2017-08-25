package com.workingbit.boardmodule.config

import com.workingbit.coremodule.config.AppProperties

/**
 * Created by Aleksey Popryaduhin on 19:25 25/08/2017.
 */
class BoardProperties : AppProperties() {

    val test: Boolean = true

    val endpoint: String = "http://localhost:8083"
}
