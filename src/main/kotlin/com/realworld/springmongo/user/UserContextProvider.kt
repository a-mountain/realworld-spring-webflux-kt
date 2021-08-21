package com.realworld.springmongo.user

import com.realworld.springmongo.security.TokenPrincipal
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component

@Component
class UserContextProvider(private val userRepository: UserRepository) {

    suspend fun getCurrentUser(): User? = getCurrentUserContext()?.user

    suspend fun getCurrentUserContext(): UserContext? {
        val context = ReactiveSecurityContextHolder.getContext().awaitSingleOrNull() ?: return null
        val tokenPrincipal = context.authentication.principal as TokenPrincipal
        val user = userRepository.findById(tokenPrincipal.userId).awaitSingle()
        return UserContext(user, tokenPrincipal.token)
    }
}

data class UserContext(
    val user: User,
    val token: String,
)