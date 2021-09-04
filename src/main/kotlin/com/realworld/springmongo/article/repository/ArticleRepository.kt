package com.realworld.springmongo.article.repository

import com.realworld.springmongo.article.Article
import com.realworld.springmongo.exceptions.InvalidRequestException
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

interface ArticleRepository : ReactiveMongoRepository<Article, String>, ArticleManualRepository {
    companion object {
        val MOST_RECENT_SORT = Sort.by(Article.CREATED_AT_FIELD_NAME).descending()
    }

    fun findBySlug(slug: String): Mono<Article>

    fun deleteBySlug(slug: String): Mono<Article>

    fun findMostRecentByAuthorIdIn(authorId: List<String>, pageable: Pageable)
}

fun ArticleRepository.findBySlugOrFail(slug: String): Mono<Article> = findBySlug(slug)
    .switchIfEmpty(InvalidRequestException("Article", "not found").toMono())

fun ArticleRepository.findMostRecentArticlesByAuthorIds(authorId: List<String>, offset: Long, limit: Int) =
    findMostRecentByAuthorIdIn(
        authorId,
        OffsetBasedPageable(limit = limit, offset = offset, sort = ArticleRepository.MOST_RECENT_SORT)
    )