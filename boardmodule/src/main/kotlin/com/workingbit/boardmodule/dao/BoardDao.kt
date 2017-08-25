package com.workingbit.boardmodule.dao

import com.workingbit.boardmodule.config.BoardProperties
import com.workingbit.coremodule.dao.BaseDao
import com.workingbit.coremodule.domain.impl.Board
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * Created by Aleksey Popryaduhin on 19:24 25/08/2017.
 */
@Component
class BoardDao @Autowired
constructor(appProperties: BoardProperties = Injekt.get())
    : BaseDao<Board>(Board::class.java, appProperties.region, appProperties.endpoint, appProperties.test)