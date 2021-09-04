package com.realworld.springmongo.api

import com.fasterxml.jackson.annotation.JsonProperty
import com.realworld.springmongo.article.Comment
import com.realworld.springmongo.user.dto.ProfileView

data class UserWrapper<T>(@JsonProperty("user") val content: T)

data class ProfileWrapper(@JsonProperty("profile") val content: ProfileView)

data class CommentWrapper(@JsonProperty("comment") val content: Comment)

data class ArticleWrapper<T>(@JsonProperty("article") val content: T)

fun <T> T.toUserWrapper() = UserWrapper(this)

fun ProfileView.toProfileWrapper() = ProfileWrapper(this)

fun Comment.toCommandWrapper() = CommentWrapper(this)

fun <T> T.toArticleWrapper() = ArticleWrapper(this)