package helpers

import com.realworld.springmongo.article.dto.CreateArticleRequest
import java.util.*

object ArticleSamples {

    const val DEFAULT_ARTICLE_BODY = "test article body"
    const val DEFAULT_ARTICLE_DESCRIPTION = "test article description"
    const val DEFAULT_ARTICLE_TITLE = "test article title"
    val DEFAULT_TAG_LIST = listOf("test_tag_1", "test_tag_2", "test_tag_3")

    fun sampleCreateArticleRequest() = CreateArticleRequest(
        body = DEFAULT_ARTICLE_BODY,
        description = DEFAULT_ARTICLE_DESCRIPTION,
        title = DEFAULT_ARTICLE_TITLE,
        tagList = DEFAULT_TAG_LIST,
    )

    fun sampleArticle() = sampleCreateArticleRequest().toArticle(
        id = UUID.randomUUID().toString(),
        authorId = UserSamples.SAMPLE_USER_ID
    )
}