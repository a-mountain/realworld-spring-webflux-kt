package com.realworld.springmongo.user.dto

import com.realworld.springmongo.user.User

data class UserView(
    val email: String,
    val token: String,
    val username: String,
    val bio: String?,
    val image: String?,
) {
    companion object {
        fun fromUserAndToken(user: User, token: String) = UserView(
            email = user.email,
            token = token,
            username = user.username,
            bio = user.bio,
            image = user.image,
        )
    }
}