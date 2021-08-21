package com.realworld.springmongo.user

import com.realworld.springmongo.exceptions.InvalidRequestException
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono


interface UserRepository : ReactiveMongoRepository<User, String> {
    fun findByEmail(email: String): Mono<User>

    fun existsByEmail(email: String): Mono<Boolean>

    fun existsByUsername(username: String): Mono<Boolean>

    fun findByUsername(username: String): Mono<User>
}

fun UserRepository.findByUsernameOrError(username: String): Mono<User> {
    return findByUsername(username)
        .switchIfEmpty(Mono.error(InvalidRequestException("Username", "not found")))
}

fun UserRepository.findByEmailOrError(email: String): Mono<User> {
    return findByEmail(email)
        .switchIfEmpty(Mono.error(InvalidRequestException("Email", "not found")))
}