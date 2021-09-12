package helpers

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriBuilder
import java.util.*

fun WebTestClient.RequestHeadersSpec<*>.authorizationToken(token: String): WebTestClient.RequestHeadersSpec<*> {
    header(HttpHeaders.AUTHORIZATION, token.formatToken())
    return this
}

fun String.formatToken() = "Token $this"

fun coCatchThrowable(lambda: suspend () -> Unit): Throwable = Assertions.catchThrowable { runBlocking { lambda() } }

fun WebTestClient.RequestHeadersUriSpec<*>.buildUri(buildUri: UriBuilder.() -> Unit): WebTestClient.RequestHeadersUriSpec<*> {
    uri { builder ->
        builder.buildUri()
        builder.build()
    }
    return this
}

fun <T> UriBuilder.queryParamIfPresent(name: String, param: T?): UriBuilder = when (param) {
    null -> queryParamIfPresent(name, Optional.ofNullable(param))
    else -> queryParam(name, param)
}