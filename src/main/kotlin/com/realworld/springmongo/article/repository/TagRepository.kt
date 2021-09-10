package com.realworld.springmongo.article.repository

import com.realworld.springmongo.article.Tag
import com.realworld.springmongo.article.toTag
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

interface TagRepository : ReactiveMongoRepository<Tag, String>

fun TagRepository.saveAllTags(tags: Iterable<String>): Mono<List<Tag>> = tags.toFlux()
    .flatMap { save(it.toTag()) }
    .onErrorContinue(DuplicateKeyException::class.java, ::emptyFunction)
    .collectList()

private fun emptyFunction(throwable: Throwable, obj: Any) {}