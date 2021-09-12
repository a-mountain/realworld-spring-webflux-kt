package com.realworld.springmongo.article.dto

import com.realworld.springmongo.article.Article
import com.realworld.springmongo.user.User
import com.realworld.springmongo.user.dto.ProfileView
import com.realworld.springmongo.user.dto.toOwnProfileView
import com.realworld.springmongo.user.dto.toProfileView
import java.time.Instant

data class ArticleView(
    val slug: String,
    val title: String,
    val description: String,
    val body: String,
    val tagList: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val favorited: Boolean,
    val favoritesCount: Int,
    val author: ProfileView,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArticleView

        if (slug != other.slug) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (body != other.body) return false
        if (tagList != other.tagList) return false
        if (favorited != other.favorited) return false
        if (favoritesCount != other.favoritesCount) return false
        if (author != other.author) return false

        return true
    }

    override fun hashCode(): Int {
        var result = slug.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + tagList.hashCode()
        result = 31 * result + favorited.hashCode()
        result = 31 * result + favoritesCount.hashCode()
        result = 31 * result + author.hashCode()
        return result
    }
}

fun Article.toArticleView(author: ProfileView, viewer: User? = null) = when (viewer) {
    null -> toUnfavoredArticleView(author)
    else -> toArticleViewForViewer(author, viewer)
}

fun Article.toArticleView(author: User, viewer: User? = null) = when (viewer) {
    null -> toUnfavoredArticleView(author.toProfileView(viewer))
    else -> toArticleViewForViewer(author.toProfileView(viewer), viewer)
}

fun Article.toUnfavoredArticleView(author: ProfileView) = toArticleView(author, favorited = false)

private fun Article.toArticleViewForViewer(author: ProfileView, viewer: User) =
    toArticleView(author, favorited = viewer.isFavoriteArticle(this))

fun Article.toAuthorArticleView(author: User) =
    toArticleViewForViewer(author.toOwnProfileView(), viewer = author)

private fun Article.toArticleView(author: ProfileView, favorited: Boolean) = ArticleView(
    slug = this.slug,
    title = this.title,
    description = this.description,
    body = this.body,
    tagList = this.tags,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    favoritesCount = this.favoritesCount,
    favorited = favorited,
    author = author
)