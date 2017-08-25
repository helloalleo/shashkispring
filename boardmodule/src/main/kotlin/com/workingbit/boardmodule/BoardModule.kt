package com.workingbit.boardmodule

import com.workingbit.boardmodule.config.BoardProperties
import com.workingbit.boardmodule.dao.BoardDao
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingleton

/**
 * Created by Aleksey Popryaduhin on 19:30 25/08/2017.
 */
object BoardModule : InjektModule {
    override fun InjektRegistrar.registerInjectables() {
        addSingleton(BoardProperties())
        addSingleton(BoardDao())
    }
}