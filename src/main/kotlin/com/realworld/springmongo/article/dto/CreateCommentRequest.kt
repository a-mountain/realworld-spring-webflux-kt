package com.realworld.springmongo.article.dto

import com.realworld.springmongo.article.Comment
import javax.validation.constraints.NotBlank

data class CreateCommentRequest(
    @field:NotBlank
    val body: String,
) {
    fun toComment(commentId: String, authorId: String) = Comment(
        id = commentId,
        authorId = authorId,
        body = body
    )
}
