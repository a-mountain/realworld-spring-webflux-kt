package com.realworld.springmongo.article.dto

data class MultipleArticlesView(
    val articles: List<ArticleView> = emptyList(),
    val articlesCount: Int = 0,
)

fun List<ArticleView>.toMultipleArticlesView() = MultipleArticlesView(this, this.size)