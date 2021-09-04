package helpers

import com.realworld.springmongo.api.ArticleWrapper
import com.realworld.springmongo.api.toArticleWrapper
import com.realworld.springmongo.article.dto.ArticleView
import com.realworld.springmongo.article.dto.CreateArticleRequest
import com.realworld.springmongo.article.dto.UpdateArticleRequest
import com.realworld.springmongo.user.dto.UserView
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

class ArticleApiSupport(private val client: WebTestClient) {

    fun createArticle(
        createRequest: CreateArticleRequest = ArticleSamples.sampleCreateArticleRequest(),
        author: UserView,
    ): ArticleView = client.post()
        .uri("/api/articles")
        .bodyValue(createRequest.toArticleWrapper())
        .authorizationToken(author.token)
        .exchange()
        .expectBody<ArticleWrapper<ArticleView>>()
        .returnResult()
        .responseBody!!
        .content

    fun getArticle(slug: String, user: UserView): ArticleView = client.get()
        .uri("/api/articles/$slug")
        .authorizationToken(user.token)
        .exchange()
        .expectBody<ArticleWrapper<ArticleView>>()
        .returnResult()
        .responseBody!!
        .content

    fun updateArticle(slug: String, request: UpdateArticleRequest, user: UserView): ArticleView = client.put()
        .uri("/api/articles/$slug")
        .bodyValue(request.toArticleWrapper())
        .authorizationToken(user.token)
        .exchange()
        .expectBody<ArticleWrapper<ArticleView>>()
        .returnResult()
        .responseBody!!
        .content

    fun deleteArticle(slug: String, user: UserView) {
        client.delete()
            .uri("/api/articles/$slug")
            .authorizationToken(user.token)
            .exchange()
    }
}