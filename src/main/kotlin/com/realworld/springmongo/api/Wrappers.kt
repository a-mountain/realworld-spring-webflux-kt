package com.realworld.springmongo.api

import com.fasterxml.jackson.annotation.JsonProperty
import com.realworld.springmongo.user.dto.ProfileView

data class UserWrapper<T>(@JsonProperty("user") val content: T)

data class ProfileWrapper(@JsonProperty("profile") val content: ProfileView)

data class CommentWrapper<T>(@JsonProperty("comment") val content: T)

data class ArticleWrapper<T>(@JsonProperty("article") val content: T)

fun <T> T.toUserWrapper() = UserWrapper(this)

fun ProfileView.toProfileWrapper() = ProfileWrapper(this)

fun <T> T.toCommentWrapper() = CommentWrapper(this)

fun <T> T.toArticleWrapper() = ArticleWrapper(this)