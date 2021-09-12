package com.realworld.springmongo.article

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Tag(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val tagName: String,
)

fun String.toTag() = Tag(tagName = this)