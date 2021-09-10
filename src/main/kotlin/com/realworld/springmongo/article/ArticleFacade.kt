package com.realworld.springmongo.article

import com.realworld.springmongo.article.dto.*
import com.realworld.springmongo.article.repository.*
import com.realworld.springmongo.exceptions.InvalidRequestException
import com.realworld.springmongo.user.User
import com.realworld.springmongo.user.UserRepository
import com.realworld.springmongo.user.dto.toOwnProfileView
import com.realworld.springmongo.user.dto.toProfileViewForViewer
import com.realworld.springmongo.user.dto.toUnfollowedProfileView
import com.realworld.springmongo.user.findAuthorByArticle
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class ArticleFacade(
    private val articleRepository: ArticleRepository,
    private val tagRepository: TagRepository,
    private val userRepository: UserRepository,
) {


    suspend fun createArticle(request: CreateArticleRequest, author: User): ArticleView {
        val id = UUID.randomUUID().toString()
        val newArticle = request.toArticle(id, author.id)
        val savedArticle = articleRepository.save(newArticle).awaitSingle()
        tagRepository.saveAllTags(savedArticle.tags).awaitSingle()
        val authorProfileView = author.toOwnProfileView()
        return savedArticle.toUnfavoredArticleView(authorProfileView)
    }

    suspend fun getArticle(slug: String, currentUser: User?): ArticleView {
        val article = articleRepository.findBySlugOrFail(slug).awaitSingle()
        val author = userRepository.findAuthorByArticle(article).awaitSingle()
        return mapToArticleView(author, article, currentUser)
    }

    suspend fun updateArticle(request: UpdateArticleRequest, slug: String, author: User): ArticleView {
        val articleToUpdate = articleRepository.findBySlugOrFail(slug).awaitSingle()

        if (!articleToUpdate.isAuthor(author)) {
            throw InvalidRequestException("Article", "only author can update article")
        }

        updateUser(request, articleToUpdate)

        val savedArticle = articleRepository.save(articleToUpdate).awaitSingle()
        return savedArticle.toAuthorArticleView(author)
    }

    suspend fun deleteArticle(slug: String, user: User) {
        val articleToDelete = articleRepository.findBySlugOrFail(slug).awaitSingle()

        if (!articleToDelete.isAuthor(user)) {
            throw InvalidRequestException("Article", "only author can delete own article")
        }

        articleRepository.deleteBySlug(slug).awaitSingle()
    }

    suspend fun findArticles(
        tag: String?,
        authorName: String?,
        favoritedByUser: String?,
        offset: Long,
        limit: Int,
        currentUser: User?,
    ): MultipleArticlesView {
        val favoritedBy = favoritedByUser?.let { userRepository.findByUsername(it).awaitSingleOrNull() }
        val author = authorName?.let { userRepository.findByUsername(it).awaitSingleOrNull() }
        return articleRepository.findNewestArticleFilteredBy(tag, author?.id, favoritedBy, limit, offset).asFlow()
            .map { mapToArticleView(it, currentUser) }
            .toList()
            .toMultipleArticlesView()
    }

    suspend fun feed(offset: Long, limit: Int, user: User): MultipleArticlesView {
        return articleRepository.findNewestArticlesByAuthorIds(user.followingIds, offset, limit).asFlow()
            .map { mapToArticleView(it, user) }
            .toList()
            .toMultipleArticlesView()
    }

    private fun updateUser(request: UpdateArticleRequest, articleToUpdate: Article) {
        request.body?.let { articleToUpdate.body = it }
        request.description?.let { articleToUpdate.description = it }
        request.title?.let { articleToUpdate.title = it }
    }

    private suspend fun mapToArticleView(article: Article, viewer: User?): ArticleView {
        val author = userRepository.findAuthorByArticle(article).awaitSingle()!!
        return mapToArticleView(author, article, viewer)
    }

    private fun mapToArticleView(author: User, article: Article, viewer: User?): ArticleView = when (viewer) {
        null -> article.toUnfavoredArticleView(author.toUnfollowedProfileView())
        else -> article.toArticleViewForViewer(author, viewer)
    }

    suspend fun favoriteArticle(slug: String, currentUser: User): ArticleView {
        val article = articleRepository.findBySlugOrFail(slug).awaitSingle()
        currentUser.favorite(article)
        userRepository.save(currentUser).awaitSingle()
        return mapToArticleView(article, currentUser)
    }

    suspend fun unfavoriteArticle(slug: String, currentUser: User): ArticleView {
        val article = articleRepository.findBySlugOrFail(slug).awaitSingle()
        currentUser.unfavorite(article)
        userRepository.save(currentUser).awaitSingle()
        return mapToArticleView(article, currentUser)
    }

    suspend fun getTags(): TagListView = tagRepository.findAll()
        .collectList()
        .awaitSingle()
        .toTagListView()

    suspend fun addComment(slug: String, content: CreateCommentRequest, currentUser: User): CommentView {
        val article = articleRepository.findBySlugOrFail(slug).awaitSingle()
        val comment = content.toComment(UUID.randomUUID().toString(), currentUser.id)
        article.addComment(comment)
        articleRepository.save(article).awaitSingle()
        return comment.toCommentView(currentUser.toOwnProfileView())
    }

    suspend fun deleteComment(slug: String, commentId: String, currentUser: User) {
        val article = articleRepository.findBySlugOrFail(slug).awaitSingle()
        val comment = article.findCommentById(commentId) ?: return
        if (!comment.isAuthor(currentUser)) {
            throw InvalidRequestException("Comment", "only author can delete comment")
        }
        article.deleteComment(comment)
        articleRepository.save(article).awaitSingle()
    }

    suspend fun getComments(slug: String, viewer: User?): MultipleCommentsView {
        val article = articleRepository.findBySlugOrFail(slug).awaitSingle()
        return article.comments.asFlow().map { comment ->
            val commentAuthor = userRepository.findById(comment.authorId).awaitSingle()
            viewer?.let { comment.toCommentView(commentAuthor.toProfileViewForViewer(viewer)) }
                ?: comment.toCommentView(commentAuthor.toUnfollowedProfileView())
        }
            .toList()
            .toMultipleCommentsView()
    }

}