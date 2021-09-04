package com.realworld.springmongo.api

import com.realworld.springmongo.article.ArticleFacade
import com.realworld.springmongo.article.dto.ArticleView
import com.realworld.springmongo.article.dto.CreateArticleRequest
import com.realworld.springmongo.article.dto.UpdateArticleRequest
import com.realworld.springmongo.user.UserSessionProvider
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ArticleController(private val articleFacade: ArticleFacade, private val userProvider: UserSessionProvider) {

    @PostMapping("/articles")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createArticle(@RequestBody request: ArticleWrapper<CreateArticleRequest>): ArticleWrapper<ArticleView> {
        val currentUser = userProvider.getCurrentUser()!!
        return articleFacade.createArticle(request.content, currentUser).toArticleWrapper()
    }

    @GetMapping("/articles/{slug}")
    suspend fun getArticle(@PathVariable slug: String): ArticleWrapper<ArticleView> {
        val currentUser = userProvider.getCurrentUser()
        return articleFacade.getArticle(slug, currentUser).toArticleWrapper()
    }

    @PutMapping("/articles/{slug}")
    suspend fun updateArticle(
        @RequestBody request: ArticleWrapper<UpdateArticleRequest>,
        @PathVariable slug: String,
    ): ArticleWrapper<ArticleView> {
        val currentUser = userProvider.getCurrentUser()!!
        return articleFacade.updateArticle(request.content, slug, currentUser).toArticleWrapper()
    }

    @DeleteMapping("/articles/{slug}")
    suspend fun deleteArticle(@PathVariable slug: String) {
        val currentUser = userProvider.getCurrentUser()!!
        articleFacade.deleteArticle(slug, currentUser)
    }
}