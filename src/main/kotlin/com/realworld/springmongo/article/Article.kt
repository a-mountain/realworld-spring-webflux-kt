package com.realworld.springmongo.article

import com.realworld.springmongo.user.User
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant

@Document
class Article(
    title: String,
    favoritesCount: Int = 0,
    @Id val id: String,
    @CreatedDate val createdAt: Instant = Instant.now(),
    @LastModifiedDate var updatedAt: Instant = Instant.now(),
    var description: String,
    var body: String,
    var authorId: String,
    @Field("comments") private val _comments: MutableList<Comment> = ArrayList(),
    @Field("tags") private val _tags: MutableList<String> = ArrayList(),
) {

    var favoritesCount: Int = favoritesCount
        private set

    val comments: List<Comment> get() = _comments

    val tags: List<String> get() = _tags

    var title: String = title
        set(value) {
            field = value
            slug = toSlug(title)
        }

    var slug: String = toSlug(title)
        private set

    fun incrementFavoritesCount() {
        favoritesCount++
    }

    fun decrementFavoritesCount() {
        favoritesCount--
    }

    fun addComment(comment: Comment) {
        _comments.add(comment)
    }

    fun deleteComment(comment: Comment) {
        _comments.remove(comment)
    }

    fun findCommentById(commentId: String): Comment? = _comments.firstOrNull { it.id == commentId }

    fun hasTag(tag: String): Boolean = _tags.contains(tag)

    fun isAuthor(authorId: String): Boolean = this.authorId == authorId

    fun isAuthor(user: User): Boolean = isAuthor(user.id)

    private fun toSlug(title: String) = title
        .lowercase()
        .replace("[&|\\uFE30-\\uFFA0’”\\s?,.]+".toRegex(), "-")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Article

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Article(id='$id', createdAt=$createdAt, updatedAt=$updatedAt, description='$description', body='$body', authorId='$authorId', favoritesCount=$favoritesCount, comments=$comments, tags=$tags, slug='$slug', title='$title')"
    }
}