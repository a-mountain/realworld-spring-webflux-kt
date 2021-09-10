package helpers

import com.realworld.springmongo.api.ArticleWrapper
import com.realworld.springmongo.api.CommentWrapper
import com.realworld.springmongo.api.toArticleWrapper
import com.realworld.springmongo.api.toCommentWrapper
import com.realworld.springmongo.article.dto.*
import com.realworld.springmongo.user.dto.UserView
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.util.*
import java.util.Optional.of
import java.util.Optional.ofNullable

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

    fun feed(user: UserView, offset: Long = 0, limit: Int = 20): MultipleArticlesView = client.get()
        .buildUri {
            path("/api/articles/feed")
            queryParam("limit", limit)
            queryParam("offset", offset)
        }
        .authorizationToken(user.token)
        .exchange()
        .expectBody<MultipleArticlesView>()
        .returnResult()
        .responseBody!!

    fun findArticles(
        tag: String? = null,
        author: String? = null,
        favoritedBy: String? = null,
        offset: Int = 0,
        limit: Int = 20,
        user: UserView? = null,
    ): MultipleArticlesView = client.get()
        .buildUri {
            path("/api/articles")
            queryParam("limit", limit)
            queryParam("offset", offset)
            queryParamIfPresent("tag", tag)
            queryParamIfPresent("author", author)
            queryParamIfPresent("favorited", favoritedBy)
        }
        .apply {
            if (user != null) {
                authorizationToken(user.token)
            }
        }
        .exchange()
        .expectBody<MultipleArticlesView>()
        .returnResult()
        .responseBody!!

    fun favoriteArticle(slug: String, user: UserView) = client.post()
        .uri("/api/articles/$slug/favorite")
        .authorizationToken(user.token)
        .exchange()
        .expectBody<ArticleWrapper<ArticleView>>()
        .returnResult()
        .responseBody!!
        .content

    fun unfavoriteArticle(slug: String, user: UserView) = client.delete()
        .uri("/api/articles/$slug/favorite")
        .authorizationToken(user.token)
        .exchange()
        .expectBody<ArticleWrapper<ArticleView>>()
        .returnResult()
        .responseBody!!
        .content

    fun addComment(slug: String, commentBody: String, user: UserView): CommentView = client.post()
        .uri("/api/articles/$slug/comments")
        .bodyValue(CreateCommentRequest(commentBody).toCommentWrapper())
        .authorizationToken(user.token)
        .exchange()
        .expectBody<CommentWrapper<CommentView>>()
        .returnResult()
        .responseBody!!
        .content

    fun deleteComment(slug: String, commentId: String, user: UserView) {
        client.delete()
            .uri("/api/articles/$slug/comments/$commentId")
            .authorizationToken(user.token)
            .exchange()
    }

    fun getComments(slug: String, user: UserView): MultipleCommentsView = client.get()
        .uri("/api/articles/$slug/comments")
        .authorizationToken(user.token)
        .exchange()
        .expectBody<MultipleCommentsView>()
        .returnResult()
        .responseBody!!

    fun getTags(): TagListView = client.get()
        .uri("/api/tags")
        .exchange()
        .expectBody<TagListView>()
        .returnResult()
        .responseBody!!
}