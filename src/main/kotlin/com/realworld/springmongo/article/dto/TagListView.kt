package com.realworld.springmongo.article.dto

import com.realworld.springmongo.article.Tag

data class TagListView(val tags: List<String> = emptyList())

fun List<Tag>.toTagListView() = TagListView(this.map(Tag::tagName))