package com.realworld.springmongo.article.repository

import com.realworld.springmongo.article.Article
import com.realworld.springmongo.lib.whereProperty
import com.realworld.springmongo.user.User
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Flux

interface ArticleManualRepository {
    fun findNewestArticleFilteredBy(
        tag: String?,
        authorId: String?,
        favoritedBy: User?,
        limit: Int,
        offset: Long
    ): Flux<Article>
}

class ArticleManualRepositoryImpl(private val mongoTemplate: ReactiveMongoTemplate) : ArticleManualRepository {

    override fun findNewestArticleFilteredBy(
        tag: String?,
        authorId: String?,
        favoritedBy: User?,
        limit: Int,
        offset: Long,
    ): Flux<Article> {
        val query = Query()
            .skip(offset)
            .limit(limit)
            .with(ArticleRepository.NEWEST_ARTICLE_SORT)

        favoritedBy?.let { query.addCriteria(authorIdEquals(it)) }
        tag?.let { query.addCriteria(tagsContains(it)) }
        authorId?.let { query.addCriteria(isFavoriteArticleByUser(it)) }

        return mongoTemplate.find(query)
    }

    private fun isFavoriteArticleByUser(it: String) = whereProperty(Article::authorId).`is`(it)

    private fun tagsContains(it: String) = whereProperty(Article::tags).all(it)

    private fun authorIdEquals(it: User) = whereProperty(Article::id).`in`(it.favoriteArticlesIds)
}