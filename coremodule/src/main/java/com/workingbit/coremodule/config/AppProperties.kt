package com.workingbit.coremodule.config

/**
 * Created by Aleksey Popryaduhin on 06:58 24/08/2017.
 */
open class AppProperties {

    val region: String
        get() = System.getenv("AWS_DEFAULT_REGION")
}