package com.realworld.springmongo.api

import com.realworld.springmongo.user.UserRepository
import com.realworld.springmongo.user.dto.toUserView
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
        assertThat(updatedUserView).isEqualTo(savedUser.toUserView(registeredUserView.token))
    }

    @Test
    fun `should return profile by name for unauthorized user`() {
        val user = userApi.signup()

        val profile = userApi.getProfile(user.username)

        assertThat(profile.username).isEqualTo(user.username)
        assertThat(profile.following).isFalse()
    }

    @Test
    internal fun `should follow and return right profile`() {
        val followingUserRequest = UserSamples.sampleUserRegistrationRequest()
            .copy(email = "testemail2@gmail.com", username = "testname2")
        val followingUser = userApi.signup(followingUserRequest)
        val follower = userApi.signup()

        val followedProfile = userApi.follow(followingUser.username, follower.token)

        assertThat(followedProfile.username).isEqualTo(followingUser.username)
        assertThat(followedProfile.following).isTrue()

        val savedFollower = userRepository.findByUsername(follower.username).block()!!
        val savedFollowingUser = userRepository.findByUsername(followingUser.username).block()!!
        val following = savedFollower.isFollowing(savedFollowingUser)
        assertThat(following).isTrue()
    }

    @Test
    fun `should unfollow and return right profile`() {
        val followingUserRequest = UserSamples.sampleUserRegistrationRequest()
            .copy(email = "testemail2@gmail.com", username = "testname2")
        val followingUser = userApi.signup(followingUserRequest)
        val follower = userApi.signup()

        val followedProfile = userApi.unfollow(followingUser.username, follower.token)

        assertThat(followedProfile.username).isEqualTo(followingUser.username)
        assertThat(followedProfile.following).isFalse()

        val savedFollower = userRepository.findByUsername(follower.username).block()!!
        val savedFollowingUser = userRepository.findByUsername(followingUser.username).block()!!
        val following = savedFollower.isFollowing(savedFollowingUser)
        assertThat(following).isFalse()
    }
}