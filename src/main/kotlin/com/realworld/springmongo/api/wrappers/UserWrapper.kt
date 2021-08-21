package com.realworld.springmongo.api.wrappers

import com.fasterxml.jackson.annotation.JsonProperty

data class UserWrapper<T>(@JsonProperty("user") val content: T)

fun <T> T.toUserWrapper() = UserWrapper(this)