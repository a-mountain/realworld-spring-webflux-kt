package com.realworld.springmongo.article.dto

import com.realworld.springmongo.validation.NotBlankOrNull

data class UpdateArticleRequest(
    @field:NotBlankOrNull
    var title: String?,
    @field:NotBlankOrNull
    var description: String?,
    @field:NotBlankOrNull
    var body: String?,
)
