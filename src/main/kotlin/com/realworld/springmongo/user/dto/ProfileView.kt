package com.realworld.springmongo.user.dto

import com.realworld.springmongo.user.User

data class ProfileView(
    val username: String,
    val bio: String?,
    val image: String?,
    val following: Boolean,
)

private fun User.toProfileView(following: Boolean) = ProfileView(
    username = this.username,
    bio = this.bio,
    image = this.image,
    following = following,
)

fun User.toUnfollowedProfileView() = this.toProfileView(following = false)

fun User.toFollowedProfileView() = this.toProfileView(following = true)

fun User.toProfileViewForViewer(viewer: User) = this.toProfileView(following = viewer.isFollowing(this))

fun User.toOwnProfileView() = this.toProfileViewForViewer(this)
