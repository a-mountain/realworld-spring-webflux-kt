package com.realworld.springmongo.user

interface UserTokenProvider {
    fun getToken(userId: String): String
}