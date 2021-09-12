package com.realworld.springmongo.api

import com.realworld.springmongo.article.Article
import com.realworld.springmongo.article.dto.UpdateArticleRequest
import com.realworld.springmongo.article.repository.ArticleRepository
import com.realworld.springmongo.article.repository.TagRepository
import com.realworld.springmongo.user.UserRepository
import helpers.ArticleApiSupport
import helpers.ArticleSamples
import helpers.UserApiSupport
import helpers.UserSamples
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant

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

    @Test
    fun `should return feed`() {
        val follower = userApi.signup()
        val followingUser = userApi.signup(UserSamples.sampleUserRegistrationRequest().copy(
            username = "following username",
            email = "following@gmail.com",
        ))
        userApi.follow(followingUser.username, follower.token)

        articleApi.createArticle(author = followingUser)
        articleApi.createArticle(author = followingUser)
        articleApi.createArticle(author = followingUser)
        articleApi.createArticle(author = follower)

        val feed = articleApi.feed(follower, offset = 1, limit = 2)
        assertThat(feed.articlesCount).isEqualTo(2)
        val hasRightAuthor = feed.articles.all { it.author.username == followingUser.username }
        assertThat(hasRightAuthor).isTrue()
    }

    @Test
    fun `should find articles`() {
        val expectedTag = "tag"
        val user1 = userApi.signup()
        val user2 = userApi.signup(UserSamples.sampleUserRegistrationRequest().copy(
            username = "test user 2",
            email = "testemail2@gmail.com"
        ))
        val article1 = articleApi.createArticle(ArticleSamples.sampleCreateArticleRequest()
            .copy(tagList = listOf(expectedTag)),
            author = user1
        )
        val article2 = articleApi.createArticle(ArticleSamples.sampleCreateArticleRequest()
            .copy(tagList = listOf(expectedTag)),
            author = user2
        )
        articleApi.createArticle(author = user2)

        val articles1 = articleApi.findArticles(tag = expectedTag, author = user1.username)
        val articles2 = articleApi.findArticles(tag = expectedTag, author = user2.username)

        assertThat(articles1.articlesCount).isEqualTo(1)
        assertThat(articles2.articlesCount).isEqualTo(1)

        assertThat(articles1.articles[0])
            .usingRecursiveComparison()
            .ignoringFieldsOfTypes(Instant::class.java)
            .isEqualTo(article1)

        assertThat(articles2.articles[0])
            .usingRecursiveComparison()
            .ignoringFieldsOfTypes(Instant::class.java)
            .isEqualTo(article2)
    }

    @Test
    fun `should favorite article`() {
        val user = userApi.signup()
        val article = articleApi.createArticle(author = user)
        val favoritedArticle = articleApi.favoriteArticle(article.slug, user)
        assertThat(article.favorited).isFalse()
        assertThat(favoritedArticle.favorited).isTrue()
        assertThat(favoritedArticle.favoritesCount).isEqualTo(1)
    }

    @Test
    fun `should unfavorite article`() {
        val user = userApi.signup()
        val article = articleApi.createArticle(author = user)
        val favoritedArticle = articleApi.favoriteArticle(article.slug, user)
        val unfavoritedArticle = articleApi.unfavoriteArticle(article.slug, user)
        assertThat(favoritedArticle.favorited).isTrue()
        assertThat(unfavoritedArticle.favorited).isFalse()
    }

    @Test
    fun `should get tags`() {
        val user = userApi.signup()
        articleApi.createArticle(ArticleSamples.sampleCreateArticleRequest()
            .copy(tagList = listOf("tag1", "tag2", "tag2")),
            author = user)
        articleApi.createArticle(ArticleSamples.sampleCreateArticleRequest()
            .copy(tagList = listOf("tag3", "tag4", "tag3")),
            author = user)
        val tags = articleApi.getTags().tags
        assertThat(tags.toSet()).isEqualTo(setOf("tag1", "tag2", "tag3", "tag4"))
    }

    @Test
    fun `should add comment`() {
        val user = userApi.signup()
        val article = articleApi.createArticle(author = user)
        val expectedBody = "test comment"
        val commentView = articleApi.addComment(article.slug, expectedBody, user)

        assertThat(commentView.body).isEqualTo(expectedBody)
        assertThat(commentView.author.username).isEqualTo(user.username)

        val savedArticle = articleRepository.findAll().blockFirst()
        assertThat(savedArticle!!.comments).isNotEmpty
    }

    @Test
    fun `should delete comment`() {
        val user = userApi.signup()
        val article = articleApi.createArticle(author = user)
        val commentView = articleApi.addComment(article.slug, "body", user)

        articleApi.deleteComment(article.slug, commentView.id, user)

        val savedArticle = articleRepository.findAll().blockFirst()!!
        assertThat(savedArticle.comments).isEmpty()
    }

    @Test
    fun `should get comments`() {
        val user = userApi.signup()
        userApi.follow(user.username, user.token)
        val article = articleApi.createArticle(ArticleSamples.sampleCreateArticleRequest(), user)
        val comment1 = articleApi.addComment(article.slug, "body 1", user)
        val comment2 = articleApi.addComment(article.slug, "body 2", user)
        val expectedComments = setOf(comment1, comment2)

        val actualComments = articleApi.getComments(article.slug, user).comments

        assertThat(actualComments.toSet()).isEqualTo(expectedComments)
    }
}