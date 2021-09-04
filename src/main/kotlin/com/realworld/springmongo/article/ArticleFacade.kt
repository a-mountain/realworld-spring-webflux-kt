package com.realworld.springmongo.article

import com.realworld.springmongo.article.dto.*
import com.realworld.springmongo.article.repository.ArticleRepository
import com.realworld.springmongo.article.repository.TagRepository
import com.realworld.springmongo.article.repository.findBySlugOrFail
import com.realworld.springmongo.article.repository.saveAllTags
import com.realworld.springmongo.exceptions.InvalidRequestException
import com.realworld.springmongo.user.User
import com.realworld.springmongo.user.UserRepository
import com.realworld.springmongo.user.dto.toOwnProfileView
import com.realworld.springmongo.user.dto.toUnfollowedProfileView
import com.realworld.springmongo.user.findAuthorByArticle
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.awaitSingle
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
        tagRepository.saveAllTags(savedArticle.tags).awaitLast()
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

    private fun updateUser(request: UpdateArticleRequest, articleToUpdate: Article) {
        request.body?.let { articleToUpdate.body = it }
        request.description?.let { articleToUpdate.description = it }
        request.title?.let { articleToUpdate.title = it }
    }

    private fun mapToArticleView(author: User, article: Article, viewer: User?): ArticleView = when (viewer) {
        null -> article.toUnfavoredArticleView(author.toUnfollowedProfileView())
        else -> article.toArticleViewForViewer(author, author)
    }
}