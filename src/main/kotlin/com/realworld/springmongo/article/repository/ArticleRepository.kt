package com.realworld.springmongo.article.repository

import com.realworld.springmongo.article.Article
import com.realworld.springmongo.exceptions.InvalidRequestException
import com.realworld.springmongo.lib.OffsetBasedPageable
import com.realworld.springmongo.lib.sortBy
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

interface ArticleRepository : ReactiveMongoRepository<Article, String>, ArticleManualRepository {
    companion object {
        val NEWEST_ARTICLE_SORT = sortBy(Article::createdAt).descending()
    }

    fun findBySlug(slug: String): Mono<Article>

    fun deleteBySlug(slug: String): Mono<Article>

    fun findByAuthorIdIn(authorId: List<String>, pageable: Pageable): Flux<Article>
}

fun ArticleRepository.findBySlugOrFail(slug: String): Mono<Article> = findBySlug(slug)
    .switchIfEmpty(InvalidRequestException("Article", "not found").toMono())

fun ArticleRepository.findNewestArticlesByAuthorIds(authorId: List<String>, offset: Long, limit: Int) =
    findByAuthorIdIn(
        authorId,
        OffsetBasedPageable(limit = limit, offset = offset, sort = ArticleRepository.NEWEST_ARTICLE_SORT)
    )