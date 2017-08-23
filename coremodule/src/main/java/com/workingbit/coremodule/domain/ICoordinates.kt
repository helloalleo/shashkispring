package com.workingbit.coremodule.domain

/**
 * Created by Aleksey Popryaduhin on 15:05 23/08/2017.
 */
interface ICoordinates {

    /**
     * rows
     */
    fun getV(): Int

    /**
     * columns
     */
    fun getH(): Int
}