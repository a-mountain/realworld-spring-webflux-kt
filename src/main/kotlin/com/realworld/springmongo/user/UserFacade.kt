package com.realworld.springmongo.user

import com.realworld.springmongo.exceptions.InvalidRequestException
import com.realworld.springmongo.user.dto.*
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import java.util.*


@Component
class UserFacade(
    private val userRepository: UserRepository,
    private val passwordService: PasswordService,
    private val userTokenProvider: UserTokenProvider,
) {

    suspend fun signup(request: UserRegistrationRequest): UserView {
        if (userRepository.existsByEmail(request.email).awaitSingle()) {
            throw emailAlreadyInUseException()
        }
        if (userRepository.existsByUsername(request.username).awaitSingle()) {
            throw usernameAlreadyInUseException()
        }
        val encodedPassword = passwordService.encodePassword(request.password)
        val id = UUID.randomUUID().toString()
        val user = request.toUser(encodedPassword, id)
        val savedUser = userRepository.save(user).awaitSingle()
        return createAuthenticationResponse(savedUser)
    }

    suspend fun login(request: UserAuthenticationRequest): UserView {
        val user = userRepository.findByEmailOrError(request.email).awaitSingle()
        if (!passwordService.matches(rowPassword = request.password, encodedPassword = user.encodedPassword)) {
            throw InvalidRequestException("Password", "invalid")
        }
        return createAuthenticationResponse(user)
    }

    suspend fun updateUser(request: UpdateUserRequest, userContext: UserContext): UserView {
        val (user, token) = userContext
        request.bio?.let { user.bio = it }
        request.image?.let { user.image = it }
        request.password?.let { user.encodedPassword = passwordService.encodePassword(it) }
        request.username?.let { updateUsername(user, it) }
        request.email?.let { updateEmail(user, it) }
        val savedUser = userRepository.save(user).awaitSingle()
        return savedUser.toUserView(token)
    }

    suspend fun getProfile(username: String, viewer: User?): ProfileView {
        val user = userRepository.findByUsernameOrError(username).awaitSingle()
        return viewer?.let { user.toProfileViewForViewer(it) } ?: user.toUnfollowedProfileView()
    }

    suspend fun follow(username: String, futureFollower: User): ProfileView {
        val userToFollow = userRepository.findByUsernameOrError(username).awaitSingle()
        futureFollower.follow(userToFollow)
        userRepository.save(futureFollower).awaitSingle()
        return userToFollow.toFollowedProfileView()
    }

    suspend fun unfollow(username: String, follower: User): ProfileView {
        val userToUnfollow = userRepository.findByUsernameOrError(username).awaitSingle()
        follower.unfollow(userToUnfollow)
        userRepository.save(follower).awaitSingle()
        return userToUnfollow.toUnfollowedProfileView()
    }

    private suspend fun updateUsername(user: User, newUsername: String) {
        if (user.username == newUsername) {
            return
        }
        if (userRepository.existsByUsername(newUsername).awaitSingle()) {
            throw usernameAlreadyInUseException()
        }
        user.username = newUsername
    }

    private suspend fun updateEmail(user: User, newEmail: String) {
        if (user.email == newEmail) {
            return
        }
        if (userRepository.existsByEmail(newEmail).awaitSingle()) {
            throw emailAlreadyInUseException()
        }
        user.email = newEmail
    }

    private fun createAuthenticationResponse(user: User): UserView {
        val token = userTokenProvider.getToken(user.id)
        return user.toUserView(token)
    }

    private fun usernameAlreadyInUseException() = InvalidRequestException("Username", "already in use")

    private fun emailAlreadyInUseException() = InvalidRequestException("Email", "already in use")
}