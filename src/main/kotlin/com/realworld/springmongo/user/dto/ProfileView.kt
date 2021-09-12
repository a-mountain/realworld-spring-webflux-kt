package com.realworld.springmongo.user.dto

import com.realworld.springmongo.user.User

data class ProfileView(
    val username: String,
    val bio: String?,
    val image: String?,
    val following: Boolean,
)

fun User.toProfileView(viewer: User? = null) = when (viewer) {
    null -> toUnfollowedProfileView()
    else -> toProfileViewForViewer(viewer)
}

fun User.toUnfollowedProfileView() = this.toProfileView(following = false)

fun User.toFollowedProfileView() = this.toProfileView(following = true)

fun User.toOwnProfileView() = this.toProfileViewForViewer(this)

private fun User.toProfileView(following: Boolean) = ProfileView(
    username = this.username,
    bio = this.bio,
    image = this.image,
    following = following,
)

private fun User.toProfileViewForViewer(viewer: User) = this.toProfileView(following = viewer.isFollowing(this))
