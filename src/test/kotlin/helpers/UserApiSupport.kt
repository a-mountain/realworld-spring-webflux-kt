package helpers

import com.realworld.springmongo.api.wrappers.UserWrapper
import com.realworld.springmongo.api.wrappers.toUserWrapper
import com.realworld.springmongo.user.dto.UpdateUserRequest
import com.realworld.springmongo.user.dto.UserAuthenticationRequest
import com.realworld.springmongo.user.dto.UserRegistrationRequest
import com.realworld.springmongo.user.dto.UserView
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

class UserApiSupport(private val client: WebTestClient) {

    fun signup(request: UserRegistrationRequest): UserView = client.post()
        .uri("/api/users")
        .bodyValue(request.toUserWrapper())
        .exchange()
        .expectBody<UserWrapper<UserView>>()
        .returnResult()
        .responseBody!!
        .content

    fun login(request: UserAuthenticationRequest): UserView = client.post()
        .uri("/api/users/login")
        .bodyValue(request.toUserWrapper())
        .exchange()
        .expectBody<UserWrapper<UserView>>()
        .returnResult()
        .responseBody!!
        .content

    fun currentUser(token: String): UserView = client.get()
        .uri("/api/user")
        .authorizationToken(token)
        .exchange()
        .expectBody<UserWrapper<UserView>>()
        .returnResult()
        .responseBody!!
        .content

    fun updateUser(token: String, request: UpdateUserRequest): UserView = client.put()
        .uri("/api/user")
        .bodyValue(request.toUserWrapper())
        .authorizationToken(token)
        .exchange()
        .expectBody<UserWrapper<UserView>>()
        .returnResult()
        .responseBody!!
        .content
}