package helpers

import com.realworld.springmongo.api.ProfileWrapper
import com.realworld.springmongo.api.UserWrapper
import com.realworld.springmongo.api.toUserWrapper
import com.realworld.springmongo.user.dto.*
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

class UserApiSupport(private val client: WebTestClient) {

    fun signup(request: UserRegistrationRequest = UserSamples.sampleUserRegistrationRequest()): UserView = client.post()
        .uri("/api/users")
        .bodyValue(request.toUserWrapper())
        .exchange()
        .expectBody<UserWrapper<UserView>>()
        .returnResult()
        .responseBody!!
        .content

    fun login(request: UserAuthenticationRequest = UserSamples.sampleUserAuthenticationRequest()): UserView =
        client.post()
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

    fun getProfile(username: String): ProfileView = client.get()
        .uri("/api/profiles/$username")
        .exchange()
        .expectBody<ProfileWrapper>()
        .returnResult()
        .responseBody!!
        .content

    fun getProfile(username: String, token: String): ProfileView = client.get()
        .uri("/api/profiles/$username")
        .authorizationToken(token)
        .exchange()
        .expectBody<ProfileWrapper>()
        .returnResult()
        .responseBody!!
        .content

    fun follow(username: String, token: String): ProfileView = client.post()
        .uri("/api/profiles/$username/follow")
        .authorizationToken(token)
        .exchange()
        .expectBody<ProfileWrapper>()
        .returnResult()
        .responseBody!!
        .content

    fun unfollow(username: String, token: String): ProfileView = client.delete()
        .uri("/api/profiles/$username/follow")
        .authorizationToken(token)
        .exchange()
        .expectBody<ProfileWrapper>()
        .returnResult()
        .responseBody!!
        .content
}