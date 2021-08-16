package helpers

import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient

fun WebTestClient.RequestHeadersSpec<*>.authorizationToken(token: String): WebTestClient.RequestHeadersSpec<*> {
    header(HttpHeaders.AUTHORIZATION, token.formatToken())
    return this
}

fun String.formatToken() = "Token $this"