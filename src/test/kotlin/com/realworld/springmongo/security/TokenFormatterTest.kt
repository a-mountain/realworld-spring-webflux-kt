package com.realworld.springmongo.security

import com.realworld.springmongo.exceptions.InvalidRequestException
import helpers.formatToken
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient


class TokenFormatterTest {
    private val formatter = TokenFormatter()

    @Test
    fun shouldThrowError() {
        val token = "1234"
        val throwable = catchThrowable { formatter.getRowToken(token) }
        assertThat(throwable)
            .isInstanceOf(InvalidRequestException::class.java)
            .hasMessage("Authorization Header: has no `Token` prefix")
    }

    @Test
    fun shouldGetRowToken() {
        val header = "Token 1234"
        val token = formatter.getRowToken(header)
        assertThat(token).isEqualTo("1234")
    }
}