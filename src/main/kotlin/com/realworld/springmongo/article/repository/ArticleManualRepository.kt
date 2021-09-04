package com.realworld.springmongo.article.repository

import com.realworld.springmongo.article.Article
import com.realworld.springmongo.user.User
import reactor.core.publisher.Flux

interface ArticleManualRepository {
    fun findMostRecentArticleFilteredBy(
        tag: String?,
        authorId: String?,
        favoritedBy: User?,
        limit: Int,
        offset: Long
    ): Flux<Article>
}