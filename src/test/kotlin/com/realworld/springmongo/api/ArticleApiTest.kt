package com.realworld.springmongo.api

import com.realworld.springmongo.article.Article
import com.realworld.springmongo.article.dto.UpdateArticleRequest
import com.realworld.springmongo.article.repository.ArticleRepository
import com.realworld.springmongo.article.repository.TagRepository
import com.realworld.springmongo.user.UserRepository
import helpers.ArticleApiSupport
import helpers.ArticleSamples
import helpers.UserApiSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArticleApiTest(
    @Autowired val client: WebTestClient,
    @Autowired val articleRepository: ArticleRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val tagRepository: TagRepository,
) {

    val articleApi = ArticleApiSupport(client)
    val userApi = UserApiSupport(client)

    @BeforeEach
    fun setUp() {
        articleRepository.deleteAll().block()
        userRepository.deleteAll().block()
        tagRepository.deleteAll().block()
    }

    @Test
    fun `should create article`() {
        val author = userApi.signup()
        val createRequest = ArticleSamples.sampleCreateArticleRequest()

        val article = articleApi.createArticle(createRequest, author)

        assertThat(article.body).isEqualTo(createRequest.body)
        assertThat(article.description).isEqualTo(createRequest.description)
        assertThat(article.title).isEqualTo(createRequest.title)
        assertThat(article.tagList).isEqualTo(createRequest.tagList)
        assertThat(article.author.username).isEqualTo(author.username)

        val savedArticle = articleRepository.findAll().collectList().block() ?: emptyList()
        assertThat(savedArticle.map(Article::title))
            .hasSize(1)
            .contains(article.title)
    }

    @Test
    fun `should return article`() {
        val user = userApi.signup()
        val slug = "article-title"
        val createRequest = ArticleSamples.sampleCreateArticleRequest().copy(title = slug)
        articleApi.createArticle(createRequest, user)

        val actual = articleApi.getArticle(slug, user)

        assertThat(actual.slug).isEqualTo(slug)
    }

    @Test
    fun `should update article`() {
        val user = userApi.signup()
        val article = articleApi.createArticle(author = user)
        val updateRequest = UpdateArticleRequest(
            body = "new body",
            description = "new description",
            title = "new title",
        )

        val updatedArticle = articleApi.updateArticle(article.slug, updateRequest, user)

        assertThat(updatedArticle.author).isEqualTo(article.author)
        assertThat(updatedArticle.body).isEqualTo(updateRequest.body)
        assertThat(updatedArticle.description).isEqualTo(updateRequest.description)
        assertThat(updatedArticle.title).isEqualTo(updateRequest.title)
    }

    @Test
    fun `should delete article`() {
        val user = userApi.signup()
        val article = articleApi.createArticle(author = user)

        articleApi.deleteArticle(article.slug, user)

        val savedArticlesCount = articleRepository.count().block()
        assertThat(savedArticlesCount).isZero()
    }
}