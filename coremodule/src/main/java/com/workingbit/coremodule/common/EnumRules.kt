package com.workingbit.coremodule.common

/**
 * Created by Aleksey Popryaduhin on 17:12 23/08/2017.
 * size: size of the desk
 * colon: how many draught on the front line
 */
enum class EnumRules(val dimension: Int, val colon: Int) {
    RUSSIAN(8, 3),
    RUSSIAN_GIVEAWAY(-8, 3),
    INTERNATIONAL(10, 4),
    INTERNATIONAL_GIVEAWAY(-10, 4);
}