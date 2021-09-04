package com.realworld.springmongo.article.dto

data class MultipleCommentsView(val comments: List<CommentView> = emptyList())

fun List<CommentView>.toMultipleCommentsView() = MultipleCommentsView(this)