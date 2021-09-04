package com.realworld.springmongo.article.repository

import com.realworld.springmongo.article.Article
import com.realworld.springmongo.user.User
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import reactor.core.publisher.Flux

class ArticleManualRepositoryImpl(private val mongoTemplate: ReactiveMongoTemplate) : ArticleManualRepository {

    override fun findMostRecentArticleFilteredBy(
        tag: String?,
        authorId: String?,
        favoritedBy: User?,
        limit: Int,
        offset: Long,
    ): Flux<Article> {
        TODO()
    }
}