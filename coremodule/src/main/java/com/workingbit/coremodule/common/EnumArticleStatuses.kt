package com.workingbit.coremodule.common

/**
 * Created by Aleksey Popryaduhin on 16:48 23/08/2017.
 */
enum class EnumArticleStatuses {
    DRAFT, // article is not approved or is editing
    APPROVED, // article is approved by me
    PUBLISHED, // article is published
    BANNED // bad article
}