package com.realworld.springmongo.user

import org.springframework.data.mongodb.core.mapping.Document

@Document
class User(
    val id: String,
    var username: String,
    var encodedPassword: String,
    var email: String,
    var bio: String? = null,
    var image: String? = null,
    private val _followingIds: MutableList<String> = ArrayList(),
    private val _favoriteArticlesIds: MutableList<String> = ArrayList(),
) {

    val followingIds: MutableList<String>
        get() = _followingIds

    val favoriteArticlesIds: List<String>
        get() = _favoriteArticlesIds

    fun follow(userId: String) {
        _followingIds.add(userId)
    }

    fun unfollow(userId: String) {
        _followingIds.remove(userId)
    }

    fun isFollowing(user: User) = _followingIds.contains(user.id)

    fun isFollower(user: User) = user.isFollowing(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "User(id='$id', username='$username', encodedPassword='$encodedPassword', email='$email', bio='$bio', image='$image', _followingIds=$_followingIds, _favoriteArticlesIds=$_favoriteArticlesIds)"
    }
}