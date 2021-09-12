package com.realworld.springmongo.user.dto

import com.realworld.springmongo.user.User

data class UserView(
    val email: String,
    val token: String,
    val username: String,
    val bio: String?,
    val image: String?,
)

fun User.toUserView(token: String) = UserView(
    email = this.email,
    token = token,
    username = this.username,
    bio = this.bio,
    image = this.image,
)