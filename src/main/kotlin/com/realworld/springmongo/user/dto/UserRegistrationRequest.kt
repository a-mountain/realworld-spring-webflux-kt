package com.realworld.springmongo.user.dto

import com.realworld.springmongo.user.User
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserRegistrationRequest(
    @field:NotBlank
    val username: String,
    @field:Email
    val email: String,
    @field:NotBlank
    val password: String,
) {
    fun toUser(encodedPassword: String, id: String) = User(
        id = id,
        encodedPassword = encodedPassword,
        email = email,
        username = username,
    )
}