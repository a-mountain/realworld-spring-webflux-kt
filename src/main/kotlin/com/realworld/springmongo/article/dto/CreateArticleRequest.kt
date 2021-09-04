package com.realworld.springmongo.article.dto

import com.realworld.springmongo.article.Article
import javax.validation.constraints.NotBlank

data class CreateArticleRequest(
    @field:NotBlank
    val title: String,
    val description: String,
    @field:NotBlank
    val body: String,
    val tagList: List<String> = emptyList(),
) {

    fun toArticle(id: String, authorId: String) = Article(
        id = id,
        authorId = authorId,
        description = description,
        title = title,
        body = body,
        _tags = ArrayList(tagList)
    )
}