package com.realworld.springmongo.user.dto

import com.realworld.springmongo.validation.NotBlankOrNull
import javax.validation.constraints.Email

data class UpdateUserRequest(
    @field:Email
    @field:NotBlankOrNull
    val email: String?,
    @field:NotBlankOrNull
    val username: String?,
    @field:NotBlankOrNull
    val password: String?,
    val image: String?,
    val bio: String?,
)
