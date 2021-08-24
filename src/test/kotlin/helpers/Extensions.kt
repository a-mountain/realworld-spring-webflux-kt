package helpers

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient

fun WebTestClient.RequestHeadersSpec<*>.authorizationToken(token: String): WebTestClient.RequestHeadersSpec<*> {
    header(HttpHeaders.AUTHORIZATION, token.formatToken())
    return this
}

fun String.formatToken() = "Token $this"

fun coCatchThrowable(lambda: suspend () -> Unit): Throwable = Assertions.catchThrowable { runBlocking { lambda() } }
