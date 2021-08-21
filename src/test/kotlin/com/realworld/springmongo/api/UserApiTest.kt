package com.realworld.springmongo.api

import com.realworld.springmongo.user.UserRepository
import com.realworld.springmongo.user.dto.UserView
import helpers.UserApiSupport
import helpers.UserSamples
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiTest(
    @Autowired val client: WebTestClient,
    @Autowired val userRepository: UserRepository,
) {

    val userApi = UserApiSupport(client)

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll().block()
    }

    @Test
    fun `should signup user`() {
        val request = UserSamples.sampleUserRegistrationRequest()
        val userView = userApi.signup(request)

        assertThat(userView.username).isEqualTo(request.username)
        assertThat(userView.email).isEqualTo(request.email)
        assertThat(userView.bio).isNull()
        assertThat(userView.image).isNull()
        assertThat(userView.token).isNotEmpty()
    }

    @Test
    fun `should login registered user`() {
        val userRegistrationRequest = UserSamples.sampleUserRegistrationRequest()
        userApi.signup(userRegistrationRequest)

        val result = userApi.login(UserSamples.sampleUserAuthenticationRequest())

        assertThat(result.username).isEqualTo(userRegistrationRequest.username)
        assertThat(result.email).isEqualTo(userRegistrationRequest.email)
        assertThat(result.bio).isNull()
        assertThat(result.image).isNull()
        assertThat(result.token).isNotEmpty()
    }

    @Test
    fun `should get current user`() {
        val response = userApi.signup(UserSamples.sampleUserRegistrationRequest())

        val currentUser = userApi.currentUser(response.token)

        assertThat(currentUser.username).isEqualTo(response.username)
        assertThat(currentUser.email).isEqualTo(response.email)
    }

    @Test
    fun `should update user`() {
        val registeredUserView = userApi.signup(UserSamples.sampleUserRegistrationRequest())

        val updateUserRequest = UserSamples.sampleUpdateUserRequest()
        val updatedUserView = userApi.updateUser(registeredUserView.token, updateUserRequest)

        assertThat(updatedUserView.email).isEqualTo(updateUserRequest.email)
        assertThat(updatedUserView.username).isEqualTo(updateUserRequest.username)
        assertThat(updatedUserView.bio).isEqualTo(updateUserRequest.bio)
        assertThat(updatedUserView.image).isEqualTo(updateUserRequest.image)

        val savedUser = userRepository.findByEmail(updatedUserView.email).block()!!
        assertThat(updatedUserView).isEqualTo(UserView.fromUserAndToken(savedUser, registeredUserView.token))
    }
}